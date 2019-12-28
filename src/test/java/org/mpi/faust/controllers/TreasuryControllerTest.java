package org.mpi.faust.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mpi.faust.dto.PaperAggregate;
import org.mpi.faust.model.Issue;
import org.mpi.faust.model.IssueState;
import org.mpi.faust.model.Paper;
import org.mpi.faust.model.Supply;
import org.mpi.faust.security.UserPrincipal;
import org.mpi.faust.service.TreasuryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
//@WebMvcTest(controllers = {TreasuryController.class})
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TreasuryControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private TreasuryService treasuryService;

    @Test
    @WithMockUser(roles = {"EMPEROR"})
    public void givenIssues_whenEmperorRequestsIssues_thenReturnIssues() throws Exception {
        {
            List<Issue> issues = Arrays.asList(new Issue(5L, IssueState.Approved,
                    Arrays.asList(new Paper(5L, 5L, 5L, 5L))));
            given(treasuryService.getIssues()).willReturn(issues);
        }

        mvc.perform(get("/api/v1/treasury/issues").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @WithMockUser(roles = {"EMPEROR"})
    public void givenIssue_whenEmperorRequestsIssue_thenReturnIssue() throws Exception {
        {
            Issue issue = new Issue(5L, IssueState.Approved,
                    Arrays.asList(new Paper(5L, 5L, 5L, 5L)));
            given(treasuryService.getIssue(5L)).willReturn(java.util.Optional.of(issue));
        }

        mvc.perform(get("/api/v1/treasury/issues/{id}", 5L).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$.id", equalTo(5)));
    }

    @Test
    @WithMockUser
    public void givenIssues_whenUserRequestsIssues_thenForbidden() throws Exception {
        {
            List<Issue> issues = Arrays.asList(new Issue(5L, IssueState.Approved,
                    Arrays.asList(new Paper(5L, 5L, 5L, 5L))));
            given(treasuryService.getIssues()).willReturn(issues);
        }

        mvc.perform(get("/api/v1/treasury/issues").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"EMPEROR"})
    public void whenEmperorDeletesIssue_thenIssueIsDeleted() throws Exception {
        mvc.perform(delete("/api/v1/treasury/issues/{id}", 5L).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        verify(treasuryService).delIssue(5L);
    }

    @Test
    @WithMockUser
    public void whenUserDeletesIssue_thenForbidden() throws Exception {
        mvc.perform(delete("/api/v1/treasury/issues/{id}", 5L).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
    }


    @Test
    @WithUserDetails(value = "emperor", userDetailsServiceBeanName = "userDetailsService")
    public void whenEmperorModifiesIssue_thenIssueIsModified() throws Exception {
        UserPrincipal userPrincipal =
                (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Issue issue = new Issue(5L, IssueState.Approved, Collections.singletonList(new Paper(5L,
                5L, 5L, 5L)));
        mvc.perform(put("/api/v1/treasury/issues/{id}", 5L).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(issue))).andExpect(status().isOk());
        verify(treasuryService).modifyIssue(issue.getId(), issue, userPrincipal);
    }

    @Test
    @WithUserDetails(value = "treasury", userDetailsServiceBeanName = "userDetailsService")
    public void whenTreasuryCreatesIssue_thenIssueIsCreated() throws Exception {
        Issue issue = new Issue(5L, IssueState.Approved,
                Arrays.asList(new Paper(5L, 5L, 5L, 5L)));
        given(treasuryService.createIssue(issue)).willReturn(issue);
        mvc.perform(post("/api/v1/treasury/issues").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(issue))).andExpect(status().isCreated());
        verify(treasuryService).createIssue(issue);
    }

    ///////////////////////////////////////////////////////

    @Test
    @WithUserDetails(value = "supplier", userDetailsServiceBeanName = "userDetailsService")
    public void givenSupply_whenSupplierRequests_thenReturnSupplies() throws Exception {
        {
            List<Supply> supplies = Arrays.asList(new Supply());
            given(treasuryService.GetAllSupplies((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal())).willReturn(supplies);
        }

        mvc.perform(get("/api/v1/treasury/supplies").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
    }


    @Test
    @WithMockUser
    public void whenSupplierRequests_thenForbidden() throws Exception {
        mvc.perform(get("/api/v1/treasury/supplies").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
    }


    @Test
    @WithUserDetails(value = "supplier", userDetailsServiceBeanName = "userDetailsService")
    public void whenSupplierDeletesSupply_thenSupplyIsDeleted() throws Exception {
        UserPrincipal userPrincipal =
                (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        mvc.perform(delete("/api/v1/treasury/supplies/{id}", 5L).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        verify(treasuryService).DeleteSupply(5L, userPrincipal);
    }

    @Test
    @WithMockUser
    public void whenUserDeletesSupply_thenForbidden() throws Exception {
        mvc.perform(delete("/api/v1/treasury/supplies/{id}", 5L).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = "supplier", userDetailsServiceBeanName = "userDetailsService")
    public void whenSupplierRequestsSupply_thenSupplyIsReturned() throws Exception {
        Supply supply = new Supply();
        supply.setId(5l);
        UserPrincipal userPrincipal =
                (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        given(treasuryService.GetSupply(5L, userPrincipal)).willReturn(java.util.Optional.of(supply));
        mvc.perform(get("/api/v1/treasury/supplies/{id}", 5L)).andExpect(status().isOk()).andExpect(jsonPath("$.id", equalTo(5)));
        verify(treasuryService).GetSupply(5L, userPrincipal);
    }


    @Test
    @WithUserDetails(value = "supplier", userDetailsServiceBeanName = "userDetailsService")
    public void whenSupplierCreatesSupply_thenSupplyIsCreated() throws Exception {
        Supply supply = new Supply();
        supply.setId(5l);
        UserPrincipal userPrincipal =
                (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        given(treasuryService.CreateSupply(supply, userPrincipal)).willReturn(supply);
        mvc.perform(post("/api/v1/treasury/supplies").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(supply))).andExpect(status().isCreated()).andExpect(jsonPath("$.id", equalTo(5)));
        verify(treasuryService).CreateSupply(supply, userPrincipal);
    }


    @Test
    @WithUserDetails(value = "supplier", userDetailsServiceBeanName = "userDetailsService")
    public void whenSupplierModifiesSupply_thenSupplyIsModified() throws Exception {
        Supply supply = new Supply();
        supply.setId(5l);
        UserPrincipal userPrincipal =
                (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        mvc.perform(put("/api/v1/treasury/supplies/{id}", 5L).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(supply))).andExpect(status().isOk());
        verify(treasuryService).UpdateSupply(5L, supply, userPrincipal);
    }


    ///////////////////////

    @Test
    @WithMockUser
    public void givenExchangeTable_whenUserRequestsExchangeTable_thenExchangeTableReturned() throws Exception {
        PaperAggregate paperAggregate = new PaperAggregate(10L, 10L, 10L);
        Collection<PaperAggregate> collection = Arrays.asList(paperAggregate);
        given(treasuryService.GetExchangeTable()).willReturn(collection);

        mvc.perform(get("/api/v1/treasury/exchanges").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
        verify(treasuryService).GetExchangeTable();
    }

    @Test
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsService")
    public void whenUserRequestsExchange_thenExchangePerformed() throws Exception {
        UserPrincipal userPrincipal =
                (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<Long, Long> map = new HashMap<>();
        map.put(5L, 5L);

        mvc.perform(post("/api/v1/treasury/exchanges").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(map))).andExpect(status().isOk()).andExpect(jsonPath("$.message", equalTo("Successfully exchanged money.")));
        verify(treasuryService).MakeExchange(map, userPrincipal);
    }
}