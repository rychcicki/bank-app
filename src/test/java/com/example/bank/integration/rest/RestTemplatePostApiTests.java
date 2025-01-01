//package com.example.bank.integration.rest;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.jdbc.Sql;
//
//@Sql({"classpath:schema.sql"/*, "classpath:data.sql"*/})
//@SpringBootTest(/*classes = BankApplication.class,*/
//        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT/*RANDOM_PORT*/)
//@ActiveProfiles("test")
//public class RestTemplatePostApiTests {
////    @LocalServerPort
////    int randomPort;
////    long clientId = 1L;
////    Client client = ClientRequestAndClientIntegrationTestUtils.clientIntegrationTestBuilder();
////
////
////    @Test
////    public void testAddClientWithBodySuccess() throws URISyntaxException {
////        RestTemplate restTemplate = new RestTemplate();
////        URI uri = new URI("http://localhost:" + randomPort + "/bank/client/" + clientId);
////        Client updatedClient = restTemplate.postForObject(uri, client, Client.class);
////        assert updatedClient != null;
////        Assertions.assertNotNull(updatedClient.getId());
////    }
////
//
//    //        @Sql({"classpath:schema.sql", "classpath:data.sql"})
//    @Test
//    void something() {
//
//        System.out.println("asdfasdfasdfsdfasdfdasf");
//    }
//}
