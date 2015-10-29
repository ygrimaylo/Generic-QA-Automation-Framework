@PlacesterWeb @unlock
Feature: Placester Web Test Cases 

Background:
    Given I launch Placester
        
@Placester_login
Scenario Outline: Login to placester web site

    And I login to plaster web site by using username <email> and password <password>
    And I access my live site <live_site_name>
    And I logout from Placester web site
    And I get browserstack results url
    
Examples:
    | email                      | password       | live_site_name         |
    | ygrimaylo@placester.com    | Password_1     | YEVGENIY GRIMAYLO SITE |