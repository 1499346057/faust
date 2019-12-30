const faker = require('faker');
const puppeteer = require('puppeteer');

let browser;
let page;

beforeAll(async () => {
    // launch browser
    browser = await puppeteer.launch(
        {
            headless: false
        }
    )
    // creates a new page in the opened browser
    page = await browser.newPage()
});

afterAll(() => {
    // browser.close()
});

describe('Business Cycle Treasury', () => {
    test('Login to treasury', async () => {
        await page.goto('http://localhost:3000/', {waitUntil: 'domcontentloaded'});
        await page.click('a.btn', {waitUntil: 'domcontentloaded'});

        await page.waitForSelector('.form');

        await page.click("input[type=text]");
        await page.type("input[type=text]", 'treasury');

        await page.click("input[type=password]");
        await page.type("input[type=password]", '123');

        await page.click("#loginButton");

        await page.waitForSelector('.container-fluid');
        const html = await page.$eval('.container-fluid', e => e.innerHTML);
        expect(html).toBe('Hello, treasury, ROLE_TREASURY');
    }, 16000);

    test('Goto add issue', async () => {
        await page.waitForSelector('.navbar');
        //await page.goto('http://localhost:3000/issues', {waitUntil: 'domcontentloaded'});
        await page.click('.navbar > a:nth-child(4)', {waitUntil: 'domcontentloaded'});

        await page.click("#addIssue");

        await page.waitForSelector('.container');
        const html = await page.$eval('.container > h2', e => e.innerHTML);
        expect(html).toBe('Add Issue');

    }, 16000);


    test('Add issue', async () => {
        await page.click("#total");
        await page.type("#total", "456");

        await page.click("#value");
        await page.type("#value", "50");

        await page.click("#rowAddButton");

        await page.evaluate(() => document.getElementById("total").value = "");

        await page.click("#total");
        await page.type("#total", "234");

        await page.click("#value");
        await page.type("#value", "1");

        await page.click("#rowAddButton");

        await page.click("#submitButton");
    }, 16000);

    test('Logout', async () => {
        await page.click('button.btn.btn-link');
    }, 16000);
});
describe('Business Cycle Emperor', () => {
    test('Login to emperor', async () => {
        await page.goto('http://localhost:3000/', {waitUntil: 'domcontentloaded'});
        await page.click('a.btn', {waitUntil: 'domcontentloaded'});

        await page.waitForSelector('.form');

        await page.click("input[type=text]");
        await page.type("input[type=text]", 'emperor');

        await page.click("input[type=password]");
        await page.type("input[type=password]", '123');

        await page.click("#loginButton");

        await page.waitForSelector('.container-fluid');
        const html = await page.$eval('.container-fluid', e => e.innerHTML);
        expect(html).toBe('Hello, emperor, ROLE_EMPEROR');
    }, 16000);

    test('Goto issues and approve', async () => {
        await page.waitForSelector('.navbar');
        await page.click('.navbar > a:nth-child(4)', {waitUntil: 'domcontentloaded'});

        await page.waitForSelector('.container-fluid');
        // const html = await page.$eval('.container > h2', e => e.innerHTML);
        // expect(html).toBe('Add Issue');
        await page.waitForSelector('#approveButton');
        await page.click('#approveButton', {waitUntil: 'domcontentloaded'});
        await page.waitFor(() => !document.querySelector('#approveButton'));
        await page.waitForSelector('td:nth-child(2)');
        const html = await page.$eval('td:nth-child(2)', e => e.innerHTML);
        expect(html).toBe('Approved');
    }, 16000);

    test('Logout for emperor', async () => {
        await page.click('button.btn.btn-link');
    }, 16000);
});

describe('Business Cycle User', () => {
    test('Login to user', async () => {
        await page.goto('http://localhost:3000/', {waitUntil: 'domcontentloaded'});
        await page.click('a.btn', {waitUntil: 'domcontentloaded'});

        await page.waitForSelector('.form');

        await page.click("input[type=text]");
        await page.type("input[type=text]", 'user');

        await page.click("input[type=password]");
        await page.type("input[type=password]", '123');

        await page.click("#loginButton");

        await page.waitForSelector('.container-fluid');
        const html = await page.$eval('.container-fluid', e => e.innerHTML);
        expect(html).toBe('Hello, user, ROLE_USER');
    }, 16000);

    test('Goto make exchange', async () => {
        await page.waitForSelector('.navbar');
        await page.click('.navbar > a:nth-child(4)', {waitUntil: 'domcontentloaded'});

        await page.waitForSelector('[name=amount]');
        await page.click("[name=amount]");
        await page.type("input[name=amount]", '123');

        await page.click("button.btn.btn-primary", {waitUntil: 'domcontentloaded'});
        await page.waitForSelector('h1');
        const html = await page.$eval('h1', e => e.innerHTML);
        expect(html).toBe('377 coins available');

    }, 16000);

    test('Logout for user', async () => {
        await page.click('button.btn.btn-link');
    }, 16000);
});


describe('Business Cycle Supplier', () => {
    test('Login to supplier', async () => {
        await page.goto('http://localhost:3000/', {waitUntil: 'domcontentloaded'});
        await page.click('a.btn', {waitUntil: 'domcontentloaded'});

        await page.waitForSelector('.form');

        await page.click("input[type=text]");
        await page.type("input[type=text]", 'supplier');

        await page.click("input[type=password]");
        await page.type("input[type=password]", '123');

        await page.click("#loginButton");

        await page.waitForSelector('.container-fluid');
        const html = await page.$eval('.container-fluid', e => e.innerHTML);
        expect(html).toBe('Hello, supplier, ROLE_SUPPLIER');
    }, 16000);

    test('Goto add supply', async () => {
        await page.waitForSelector('.navbar');
        //await page.goto('http://localhost:3000/issues', {waitUntil: 'domcontentloaded'});
        await page.click('.navbar > a:nth-child(4)', {waitUntil: 'domcontentloaded'});

        await page.click("a.btn.btn-success");

        await page.waitForSelector('.container');
        const html = await page.$eval('.container > h2', e => e.innerHTML);
        expect(html).toBe('Add supply');

    }, 16000);


    test('Add supply', async () => {
        await page.click("#good");
        await page.type("#good", "Sobaka");

        await page.click("#price");
        await page.type("#price", "105");

        await page.click("#rowAddButton");

        await page.evaluate(() => document.getElementById("good").value = "");

        await page.click("#submitButton");
    }, 16000);

    test('Logout for supplier', async () => {
        await page.click('button.btn.btn-link');
    }, 16000);
});


describe('Business Cycle Treasury step 2', () => {
    test('Login to treasury', async () => {
        await page.goto('http://localhost:3000/', {waitUntil: 'domcontentloaded'});
        await page.click('a.btn', {waitUntil: 'domcontentloaded'});

        await page.waitForSelector('.form');

        await page.click("input[type=text]");
        await page.type("input[type=text]", 'treasury');

        await page.click("input[type=password]");
        await page.type("input[type=password]", '123');

        await page.click("#loginButton");

        await page.waitForSelector('.container-fluid');
        const html = await page.$eval('.container-fluid', e => e.innerHTML);
        expect(html).toBe('Hello, treasury, ROLE_TREASURY');
    }, 16000);

    test('Goto check money', async () => {
        await page.waitForSelector('.navbar');
        //await page.goto('http://localhost:3000/issues', {waitUntil: 'domcontentloaded'});
        await page.click('.navbar > a:nth-child(3)', {waitUntil: 'domcontentloaded'});

        await page.waitForSelector('h1');
        const html = await page.$eval('h1', e => e.innerHTML);
        expect(html).toBe('223 coins available');

    }, 16000);

    test('Goto approve supply', async () => {
        await page.waitForSelector('.navbar');
        //await page.goto('http://localhost:3000/issues', {waitUntil: 'domcontentloaded'});
        await page.click('.navbar > a:nth-child(5)', {waitUntil: 'domcontentloaded'});

        await page.waitForSelector('#approveButton');
        await page.click('#approveButton', {waitUntil: 'domcontentloaded'});
        await page.waitFor(() => !document.querySelector('#approveButton'));

    }, 16000);

    test('Logout for supplier', async () => {
        await page.click('button.btn.btn-link');
    }, 16000);
});

describe('Business Cycle Supplier v2', () => {
    test('Login to supplier', async () => {
        await page.goto('http://localhost:3000/', {waitUntil: 'domcontentloaded'});
        await page.click('a.btn', {waitUntil: 'domcontentloaded'});

        await page.waitForSelector('.form');

        await page.click("input[type=text]");
        await page.type("input[type=text]", 'supplier');

        await page.click("input[type=password]");
        await page.type("input[type=password]", '123');

        await page.click("#loginButton");

        await page.waitForSelector('.container-fluid');
        const html = await page.$eval('.container-fluid', e => e.innerHTML);
        expect(html).toBe('Hello, supplier, ROLE_SUPPLIER');
    }, 16000);

    test('Goto check money', async () => {
        await page.waitForSelector('.navbar');
        //await page.goto('http://localhost:3000/issues', {waitUntil: 'domcontentloaded'});
        await page.click('.navbar > a:nth-child(3)', {waitUntil: 'domcontentloaded'});

        await page.waitForSelector('h1');
        const html = await page.$eval('h1', e => e.innerHTML);
        expect(html).toBe('105 coins available');

    }, 16000);

});
