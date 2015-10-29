@PlacesterServicesAPI
Feature: Placester Services API Test Cases 

@Placester_Services_API_Account
Scenario Outline: Testing placester restful services api, related to account

    And I create account <account_email> and validate response code 200 and capture account_id
    And I read account by account id with account_id <account_id> and validate response code 200 and response text <account_email>
    #And I read account by email with account_id <account_id> and validate response code 200 and response text <account_email>
    And I update account with account_id <account_id> and validate response code 200 and response text TestName_updated
    #And I delete account with account_id <account_id> and validate response code 204 and response text is blank
    
Examples:
    | account_email    			| account_id                           | 
    | test10@test.tv   			| 76ca2fe4-d7fc-4d74-acbc-bd019c7c70a6 |
    #| test1@test.tv   			| 2beef429-1c8e-4922-a001-651841017b8e |
    #| test2@test.tv   			| 59df0e08-c52b-4156-a22c-80a827d4a67e |
    #| test3@test.tv   			| 31cad30f-337f-479f-95d0-4b699160b4d5 |
    #| test4@test.tv   			| 3742ed25-78fa-4376-b8fe-c6fe61c16856 |
    #| test7@test.tv   			| 1b30bc70-e0a6-4df5-b72f-c7361b0ca194 |
    #| test8@test.tv   			| bcc2c9f2-7880-4660-ac62-7637265c333c |
    #| test9@test.tv   			| 23bd9190-6773-4b71-8380-bd9808dd4f2e |

@Placester_Services_API_Account_Type
Scenario Outline: Testing placester restful services api, related to account type

    And I create account type <account_type> with account_id <account_id> and validate response code 200 and response text <account_type>
    And I read account type <account_type> with account_id <account_id> and validate response code 200 and response text <account_type>
    And I update account_type <account_type> with account_id <account_id> and validate response code 200 and response text <account_type>
    And I delete account_type and validate response code 204
    
Examples:
    | account_type    			| account_id                           | 
    | user   			        | 23bd9190-6773-4b71-8380-bd9808dd4f2e |
    | agent   			        | 23bd9190-6773-4b71-8380-bd9808dd4f2e |
    | broker   			        | 23bd9190-6773-4b71-8380-bd9808dd4f2e |
    | publisher   			| 23bd9190-6773-4b71-8380-bd9808dd4f2e |
    | association   			| 23bd9190-6773-4b71-8380-bd9808dd4f2e |
    | brand   			        | 23bd9190-6773-4b71-8380-bd9808dd4f2e |

@Placester_Services_API_Account_Address
Scenario Outline: Testing placester restful services api, related to account address

   And I create account group for account_email <account_email> with account address data <region>,<primary_address>,<postal_code>,<locality>,<address1>,<address2>,<country_code>,<phone>,<group_name>,<group_type> and validate response code 200
   And I create account address for account_id <account_id> with account address data <region>,<primary_address>,<postal_code>,<locality>,<address1>,<address2>,<country_code> and validate response code 200
   And I read account address and validate response code 200 and response text <region>,<primary_address>,<postal_code>,<locality>,<address1>,<address2>,<country_code> 
   And I update account address for account id <account_id> with account address data <region>,<primary_address>,<postal_code>,<locality>,<address1>,<address2>,<country_code> and validate response code 200
   And I delete account_address and validate response code 204 and response text is blank
    
    
Examples:
    | account_id                           | region | primary_address | postal_code | locality | address1         | address2        | country_code | account_email  | phone        | group_name  | group_type |
    | 76ca2fe4-d7fc-4d74-acbc-bd019c7c70a6 | MA     | true            | 02142       | Boston   | 123 Street       | unit 2          | US           | test10@test.tv | 555-555-5555 | QATestGroup | broker     |

@Placester_Services_API_Account_Profile
Scenario Outline: Testing placester restful services api, related to account address

   And I create account group for account_email <account_email> with account address data <region>,<primary_address>,<postal_code>,<locality>,<address1>,<address2>,<country_code>,<phone>,<group_name>,<group_type> and validate response code 200
   And I create account profile for account_email <account_email> and account_id <account_id> with account address data <region>,<primary_address>,<postal_code>,<locality>,<address1>,<address2>,<country_code>,<phone> and validate response code 200
   And I read account profile and validate response code 200 and response text <region>,<primary_address>,<postal_code>,<locality>,<address1>,<address2>,<country_code>,<phone>
   And I update account profile for account_email <account_email> with account_id <account_id> and validate response code 200
   And I delete account_profile and validate response code 204 and response text is blank
   
    
    
Examples:
    | account_id                           | account_email       | region | primary_address | postal_code | locality | address1         | address2        | country_code | phone        | group_name  | group_type |
    | 76ca2fe4-d7fc-4d74-acbc-bd019c7c70a6 | test10@test.tv      |  MA    | true            | 02142       | Boston   | 123 Street       | unit 2          | US           | 555-555-5555 | QATestGroup | broker     |

@Placester_Services_API_Account_Group
Scenario Outline: Testing placester restful services api, related to account address

   And I create account group for account_email <account_email> with account address data <region>,<primary_address>,<postal_code>,<locality>,<address1>,<address2>,<country_code>,<phone>,<group_name>,<group_type> and validate response code 200
   And I read account group by id for account_id <account_id> with account_email <account_email> and validate response code 200 and response text <region>,<primary_address>,<postal_code>,<locality>,<address1>,<address2>,<country_code>,<phone>,<group_name>,<group_type>
   And I read account group by email for account_id <account_id> with account_email <account_email> and validate response code 200 and response text <region>,<primary_address>,<postal_code>,<locality>,<address1>,<address2>,<country_code>,<phone>,<group_name>,<group_type>
  And I read account group by account id for account_id <account_id> with account_email <account_email> and validate response code 200 and response text <region>,<primary_address>,<postal_code>,<locality>,<address1>,<address2>,<country_code>,<phone>,<group_name>,<group_type>
  And I update account group for group_name <group_name> with group_type <group_type> and validate response code 200
  And I delete account_group and validate response code 204 and response text is blank
      
Examples:
    | account_id                           | account_email       | region | primary_address | postal_code | locality | address1         | address2        | country_code | phone        | group_name  | group_type |
    | 76ca2fe4-d7fc-4d74-acbc-bd019c7c70a6 | test10@test.tv      |  MA    | true            | 02142       | Boston   | 123 Street       | unit 2          | US           | 555-555-5555 | QATestGroup | broker     |

@Placester_Services_API_Account_Media
Scenario Outline: Testing placester restful services api, related to account media

   And I create account group for account_email <account_email> with account address data <region>,<primary_address>,<postal_code>,<locality>,<address1>,<address2>,<country_code>,<phone>,<group_name>,<group_type> and validate response code 200
   And I create account media for account email <account_email> with account_id <account_id> and validate response code 200 and response_text <media_url>
   And I read account media with account_id <account_id> and validate response code 200 and response text <media_url>
   And I update account_media for account id <account_id> and validate response code 200 and response text <media_url>
   And I delete account_media and validate response code 204 and response text is blank
   
      
Examples:
    | account_id                           | account_email       | media_url     | region | primary_address | postal_code | locality | address1         | address2        | country_code | phone        | group_name  | group_type |
    | 76ca2fe4-d7fc-4d74-acbc-bd019c7c70a6 | test10@test.tv      | www.yahoo.com | MA     | true            | 02142       | Boston   | 123 Street       | unit 2          | US           | 555-555-5555 | QATestGroup | broker     |

@Placester_Services_API_Account_Meta
Scenario Outline: Testing placester restful services api, related to account meta

   And I create account group for account_email <account_email> with account address data <region>,<primary_address>,<postal_code>,<locality>,<address1>,<address2>,<country_code>,<phone>,<group_name>,<group_type> and validate response code 200
   And I create account meta for account_id <account_id> with meta_value <meta_value> and validate response code 200 and response_text <meta_value>
   And I read account meta by id with account_id <account_id> and validate response code 200 and response text <meta_value>
   And I read account meta by account_id with account_id <account_id> and validate response code 200 and response text <meta_value>
   And I update account_meta for account id <account_id> and validate response code 200 and response text <meta_value>
   And I delete account_meta and validate response code 204 and response text is blank
   
      
Examples:
    | account_id                           | meta_value | region | primary_address | postal_code | locality | address1         | address2        | country_code | phone        | group_name  | group_type |
    | 76ca2fe4-d7fc-4d74-acbc-bd019c7c70a6 | meow       | MA     | true            | 02142       | Boston   | 123 Street       | unit 2          | US           | 555-555-5555 | QATestGroup | broker     |

@Placester_Services_API_Account_Permission
Scenario Outline: Testing placester restful services api, related to account permission

   And I create account group for account_email <account_email> with account address data <region>,<primary_address>,<postal_code>,<locality>,<address1>,<address2>,<country_code>,<phone>,<group_name>,<group_type> and validate response code 200
   And I create account permission for account_id <account_id> with permission_value <permission_value> and validate response code 200
   And I read account permission by id for account_id <account_id> and validate response code 200 and response text <permission_value>
   And I read account permission by account_id for account_id <account_id> and validate response code 200 and response text <permission_value>
   And I update account permission for account_id <account_id> with permission_value <permission_value> and validate response code 200
   And I delete account_permission and validate response code 204 and response text is blank
      
Examples:
    | account_id                           | account_email       | region | primary_address | postal_code | locality | address1         | address2        | country_code | phone        | group_name  | group_type | permission_value |
    | 76ca2fe4-d7fc-4d74-acbc-bd019c7c70a6 | test10@test.tv      |  MA    | true            | 02142       | Boston   | 123 Street       | unit 2          | US           | 555-555-5555 | QATestGroup | broker     | 7		      |

@Placester_Services_API_Account_Phone
Scenario Outline: Testing placester restful services api, related to account phone

   And I create account group for account_email <account_email> with account address data <region>,<primary_address>,<postal_code>,<locality>,<address1>,<address2>,<country_code>,<phone>,<group_name>,<group_type> and validate response code 200
   And I create account phone by group_id for account_id <account_id> with account phone data <label>,<phone>,<primary_phone> and validate response code 200
   And I create account phone by account_id for account_id <account_id> with account phone data <label>,<phone>,<primary_phone> and validate response code 200
   And I read account phone for account_id <account_id> and validate response code 200 and response text <label>,<phone>,<primary_phone>
   And I update account phone for account_id <account_id> with account phone data <label>,<phone>,<primary_phone> and validate response code 200
   And I delete account_phone and validate response code 204 and response text is blank
      
Examples:
    | account_id                           | account_email       | region | primary_address | postal_code | locality | address1         | address2        | country_code | phone        | group_name  | group_type | label | phone        | primary_phone |
    | 76ca2fe4-d7fc-4d74-acbc-bd019c7c70a6 | test10@test.tv      |  MA    | true            | 02142       | Boston   | 123 Street       | unit 2          | US           | 555-555-5555 | QATestGroup | broker     | cell  | 555-555-5555 | false         | 

@Placester_Services_API_Account_Session
Scenario Outline: Testing placester restful services api, related to account session
   And I create account session by account_email for account_id <account_id> with account_email <account_email> and validate response code 200
   And I create account session by account_id for account_id <account_id> with account_email <account_email> and validate response code 200
   And I create account session by verbose for account_id <account_id> with account_email <account_email> and validate response code 200
   And I read account session by id for account_id <account_id> and validate response code 200 and response text <account_id>
   And I read account session by account_id for account_id <account_id> and validate response code 200 and response text <account_id>
   And I read account session by verbose for account_id <account_id> and validate response code 200 and response text <account_email>
   And I delete account_session by id with account_id <account_id> and validate response code 204 and response text is blank
   And I delete account_session by account_id with account_id <account_id> and validate response code 204 and response text is blank
      
Examples:
    | account_id                           | account_email       |
    | 76ca2fe4-d7fc-4d74-acbc-bd019c7c70a6 | test10@test.tv      | 

@Placester_Services_API_Account_ConvertCsvToJson
Scenario: Testing placester restful services api, related to conversion of csv to json

   And I convert csv to json and validate response code 200

@Placester_Services_API_Billing_Account_Create
Scenario Outline: Testing placester restful services api, related to billing account

   And I create billing account for account_id <account_id> with billing_account_data <account_email>,<first_name>,<last_name>,<company_name>,<state>,<address1>,<address2>,<country>,<phone>,<zip>,<city> and validate response code 200
   And I read billing account by id for account_id <account_id> and validate response code 200 and response text <account_email>,<first_name>,<last_name>,<company_name>,<state>,<address1>,<address2>,<country>,<phone>,<zip>,<city>
   And I read billing account by account_id for account_id <account_id> and validate response code 200 and response text <account_email>,<first_name>,<last_name>,<company_name>,<state>,<address1>,<address2>,<country>,<phone>,<zip>,<city>
   And I update billing account for account_id <account_id> with billing_account_data <account_email>,<first_name>,<last_name>,<company_name>,<state>,<address1>,<address2>,<country>,<phone>,<zip>,<city> and validate response code 200
   And I delete billing_account and validate response code 204 and response text is blank
      
Examples:
    | account_id                           | account_email       | first_name  | last_name  | company_name | city     | address1         | address2        | country | phone        | state  | zip   |
    | 76ca2fe4-d7fc-4d74-acbc-bd019c7c70a6 | test10@test.tv      | QAFirstName | QALastName | Placester    | Boston   | 123 Street       | unit 2          | US      | 555-555-5555 | MA     | 02210 |   

@Placester_Services_API_Account_Session
Scenario Outline: Testing placester restful services api, related to account session
   And I create account session by account_email for account_id <account_id> with account_email <account_email> and validate response code 200
   And I create account session by account_id for account_id <account_id> with account_email <account_email> and validate response code 200
   And I create account session by verbose for account_id <account_id> with account_email <account_email> and validate response code 200
   And I read account session by id for account_id <account_id> and validate response code 200 and response text <account_id>
   And I read account session by account_id for account_id <account_id> and validate response code 200 and response text <account_id>
   And I read account session by verbose for account_id <account_id> and validate response code 200 and response text <account_email>
   And I delete account_session by id with account_id <account_id> and validate response code 204 and response text is blank
   And I delete account_session by account_id with account_id <account_id> and validate response code 204 and response text is blank
      
Examples:
    | account_id                           | account_email       |
    | 76ca2fe4-d7fc-4d74-acbc-bd019c7c70a6 | test10@test.tv      | 

@Placester_Services_API_Billing_Coupon
Scenario Outline: Testing placester restful services api, related to billing service coupon

   And I create billing coupon with coupon data <coupon_name>,<coupon_description>,<discount_percent>,<code_id>,<plan_id> and validate response code 200

Examples:
    | coupon_name     | plan_id                              | code_id    | discount_percent | coupon_description |
    | One time coupon | 0045276f-a80d-45ce-8d8a-bf53b33603f2 | QATestCode | 40               | One Time Coupon    ||

     