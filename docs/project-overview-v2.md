# Project Overview
This is an IT project for insurance company that is under digital transformation.
Aim of this project is to rewrite existing legacy MOTOR insurance calculator (sales tool) to new tech stack. Product is now long time on the market and with deep knowledge of this business due to its' history might be able to redesign system's architecture for a lot more alignment with business needs and company's strategy.

# System users
Users of this system are insurance agents, insurer's call center internal employees and direct clients.
Every type of user has certain needs but we also decide what functionalities or what data are available to which user types. For example only call center employee is able to see into claims history of customer, his driving records and customer segmentation insights. Since insurance agents are not insurer's direct employees hence they are not allowed to get such insights (otherwise they could potentially sell those data to 3rd parties). Customer is the most limited in this matter as customers are not regulated by any means compared to insurance agents.

# Business requirements
1. Ability to get data from user via form. We ask questions that will give answers that can be split into two groups:
   1. Underwriting and offering recommendation. They will be input data for underwriting rules (risk calculation) or insurance options recommendation (fitting offering to customer needs, segmentation, buying history etc.). 
   2. Operational information. E.g. name and surname. This information are not part of underwriting rules itself. We need them to have customer record and to know who to contact and address in our communications. On the contrary, customer's age is needed for underwriting and would be assigned to the first group.
2. Integration with quotation engine to discover available insurance options and their price. 
3. Handle entering calculator with context data and context recognition:
   - Type of user. Could be also defined as sales channel: AGENT, CALL CENTER, DIRECT.
   - Customer context (optional, since this might the customer first ever contact with this insurer).
   - Previous insurance policy. It would then trigger strategies that handle renewal sales.
   - Marketing campaign that will result in a discount.
4. Recommend options that customer might be interested in.
5. Ability to provide different sales strategies depending on user type (sales channel), customer segment.
6. Interaction with the offering options in an accessible way. Tariff/pricing engine provides a lot of options but are covering all of this complexity with recommendations, clean and intuitive UI. Backend model is designed to manipulate insurance scopes, adjusting protection scope to customer needs.
7. Ability to drop-off offering but resume it withing given time e.g. 3 days.
8. Ability to apply discounts on the offering. E.g. promo codes, but insurance agents also have their own discount budgets for some of their clients. They need the ability to have insights about available discounting options and ability to simulate price after discount application.
9. Ability to quickly simulate changes in offering and how it affects price.
10. Checkout that is quick and intuitive.
11. Product configuration feature that will affect what questions we ask for risk assessment and recommendation strategy.
12. Ability to print policy documents on the checkout stage.
13. Ability to enrich and transform data from user's form using 3rd party services from government, data providers and our internal shared services e.g. customer repository, customer and agent scoring etc.
14. System needs to be highly extensible when we will either discover new sales strategies or get access to new data sources. This will result in new rules to be applied on the offering and recommendation algorithms.
15. Module that handles form with questions needs to be highly configurable since this changes frequently. Tariff team often comes up with suggestions on new strategies to ask smart questions that give us insights for risk assessment.
# Architecture
## General assumptions
- Focused on clear module boundaries. Separation around business capabilities. That will assure consistent long-term development velocity and ability to scale independently if this product brings more revenue to the table.
    - You could pretty much say it's aligned with Domain-Driven Design.
- Integration patterns are
- Each module will have its own database schema, but not own database. This level of isolation brings more cohesion between modules and still enables us to extract to separate databases in the future.

## System's limitations and its environment
### TIA - Core Policy Management System
Our most significant limitation is upstream core policy management system called TIA.

- Technically it's Oracle DBMS.
- It owns a large part of product's logic in its PL/SQL code including. It serves as an application layer.
- It owns partial configuration of the product, dictionaries that have enumerations of customer types, vehicle types, vehicle configurations including makes and models.
- It serves as a proxy to the pricing engine, Earnix.
- We integrate directly with this Oracle database as it is our **core policy management system** called "TIA", but we don't directly integrate with Earnix. TIA does it on our behalf when given particular action to execute eg. "calculateQuotation".

### 3rd party services
1. UFG - Ubezpieczeniowy Fundusz Gwarancyjny. Polish financial and insurance regulator's API service which provides claims history or insurance status of vehicles.
2. CEPiK - Centralna Ewidencja Pojazdów i Kierowców.
    - API service providing information about:
        - Vehicles data, vehicle history vehicle ownership
        - Driver's data, driver's penalty points, driver licenses
    - Owned by Polish government.
3. GUS - Główny Urząd Statystyczny. API service providing information about organizations in Poland. It is needed during sales for when customer is organizational. We check credibility of such organization and its business profile to check if vehicles won't be used for risky operations e.g. people transportation. Risk is then much higher than using such vehicle for daily commute.

## Modules / Sub-domains
Modules are designed around business capabilities.

### Data procurement
Module that handles the form with questions.
It aggregates context data from different sources and provides it to other modules.
It uses 3rd party services to enrich data and transform it into context ready for the offering module.
It's driven by requirements from the product team to gather specific information required for risk assessment.

#### Data enrichment / data transformation
It's a pipe or a transformer that takes initial context and filled form as input and return output after querying external databases and services. Output from this module is input for Offering module that is needed to calculate price.
E.g. form asks user to fill in the VIN number, but underneath we query 3rd party services about this VIN number and get insigts about this vehicle to properly assess risks.

This module needs to be able to run transformation rules partially or in isolation and aggregate deeper context and knowledge. Representation of such context accumulation needs to be available to forms since they are dynamic and contextual.
E.g if customer is an organization we run query to government services by the taxpayer identification number and if response reveals something shady about this organization, we would need to ask more questions to mitigate risks while still be able to create an offering to the customer. It means that some questions are conditional of some data transformations.

### Offering
Model of all available options, additional insurances, additional options for chosen insurances.
This model is designed to be interacted with. It awaits changes in insurance scope to inform about price changes and benefits of such changes. It is the heart of this product's sales and intelligent advisor for insurance agents, insurer's call center employees or direct customers.

Offering model is able to answer questions like:
1. What will be the price if I take this additional insurance?
2. What will be the price if I take this additional risk to this insurance?
3. What will be the price if I change risk sum for this particular risk?
4. What would be optimal insurance scope for this customer or what would be such customer eager to buy?
    - Disclaimer: it would need additional data about client segmentation and/or scoring. E.g. if we have knowledge that this customer has history of insuring vehicles with notable value or that customer already has several insurances for his estates, then we have a clear insight that this might be a wealthy customer who will prioritize more protection and larger insurance sums despite higher price.

Offering model collects all data of its sales and runs analytics over the data to gain insights with which it is able to give such recommendations:
1. 80% of customers that bought this isurance, also added this option to get optimal protection.
2. 70% of customers that were insuring a vehicle of this class (based on vehicle worth) were happy with these options to get optimal protection.

### Checkout
Checkout module is the finalization of sales. Getting to this stage means customer has accepted the offering - isurance scope, terms, chose all interesting options and is now ready to finalize transaction (pay for his insurance).

At this stage we:
1. Gather additional data for after-sales care and operations. We pass this information to core policy system.
2. Handle the payment.
3. Print insurance policy documents.
    - We use existing, external module for this.
4. Gather agreements for marketing contact.

---

# Tech stack
- Java 21
- Angular 18
- Spring Boot 3.4
