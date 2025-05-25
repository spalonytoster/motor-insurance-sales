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
2. Integration with pricing engine to discover available insurance options and their price. 
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

### Pricing, Tariff
This domain is handles by Earnix which we cannot access directly. Only via TIA.

### 3rd party services
1. UFG - Ubezpieczeniowy Fundusz Gwarancyjny. Polish financial and insurance regulator's API service which provides claims history or insurance status of vehicles.
2. CEPiK - Centralna Ewidencja Pojazdów i Kierowców.
    - API service providing information about:
        - Vehicles data, vehicle history vehicle ownership
        - Driver's data, driver's penalty points, driver licenses
    - Owned by Polish government.
3. GUS - Główny Urząd Statystyczny. API service providing information about organizations in Poland. It is needed during sales for when customer is organizational. We check credibility of such organization and its business profile to check if vehicles won't be used for risky operations e.g. people transportation. Risk is then much higher than using such vehicle for daily commute.

## Modules / Sub-domains
As this project embraces Domain-Driven Design concepts, modules are very much designed around business capabilities.

### Customer Journey
Master process that oversees the whole sales instance that users interacts with. It's like a main process manager.
If user abandons the process at any stage, this is the module that keeps the state and allows to resume the sales process.
It also handles omnichannel feature. It means it's sales channel aware and enables user to enter the sales process from any sales channel e.g. Agent & Broker channel, Direct Customer channel and Contact Center channel.
In terms of DDD, it's the aggregate root keeping references to sub-processes aggregates.

### Data Procurement
Responsible for collection of data needed to feed external pricing engine on the offering stage.
It consists of several small submodules described below.
This module is a sub-process that handles all data procurement and accumulated its data in an object called Calculation Context.
It aggregates context data from different sources and provides it to other modules.

#### Forms
Module that handles interaction with the user.
It receives user filled form fields with answer to risk related questions.
Questions in forms are driven by requirements from the product team. They gather specific information required for risk assessment.
REST controllers handling forms can be imagined as receptors that continuously receive input signals. Then thos inputs go through transformation pipes. 

#### Data enrichment / data transformation
On high-level it acts as a pipe or transformer that takes input (e.g. form fields from user) and returns output after querying external databases and services.
On lower level it's consisting of a set of small transformations. The data transformers should be understood as pure functions where they take `Input` and calculate `Output`.
Transformers are set in data enrichment and are triggered by received inputs.

All outputs are accumulated in a single value object called Calculation Context.
After all required data have been filled and user is ready to proceed, whole Calculation Context (accumulator of all outputs) is being passed as input to Offering module.

Example transformation - VinToVehicleHistoryTransformer:
Form asks user to fill in the VIN number (input). Data enrichment looks for a Transformer matching input type (VIN).
After it's found, the transformer is invoked with the VIN. It's implemented as a query to 3rd party service that receives vehicle history.
Result from this service is mapped to expected output of this transformer and then set in accumulator object (Calculation Context).

Representation of accumulated context or some parts of it need to be available to forms since they are dynamic and contextual.
Example of contextuality: if customer is an organization we run query to government services by the taxpayer identification number and if response reveals something shady about this organization, we would need to ask more questions to mitigate risks while still being able to do quotation for the customer. It means that some questions are conditional of some data transformations.

#### Vehicle Specification
Specific part of the user form that interacts with user to specify exact vehicle configuration - make, model, production year, vehicle version etc.
It's been extracted to a dedicated module because of high reusability chance in other insurances like car fleets or cars dealers.
This module serves whole vehicle specification as it's output. It can be set in Calculation Context without further transformations.

### Offering
Model responsible for serving all available coverage options, additional insurances, additional options for chosen insurances.
It also serves simple rule-based recommendations to the user (agent or call center employee) based on conversion rates for particular coverage options.
This model is designed to be interacted with. It awaits changes in insurance scope to inform about price changes and benefits of such changes. It is the heart of this product's sales and intelligent advisor for insurance agents, insurer's call center employees or direct customers.

Offering model is able to answer questions like:
1. What will be the price if I take this additional insurance (simulations)?
2. What will be the price if I choose this additional coverage option for this insurance?
3. What will be the price if I change risk sum for this particular risk?
4. What would be optimal insurance scope for this customer or what would be such customer eager to buy?
    - Disclaimer: it would need additional data about client segmentation and/or scoring. Such insights should be delivered by pricing engine. E.g. if we have knowledge that this customer has history of insuring vehicles with notable value or that customer already has several insurances for his estates, then we have a clear insight that this might be a wealthy customer who will prioritize more protection and larger insurance sums despite higher price.

Offering model collects all data of its sales to feed analytics to the pricing engine.

Rule-based recommendations can be implemented without data from pricing engine. For example:
1. 80% of customers that bought this insurance, also added this option to get optimal protection.
2. 70% of customers that were insuring a vehicle of this class (based on vehicle worth) were happy with these options to get optimal protection.

### Checkout
Checkout module is the finalization of sales. Getting to this stage means customer has accepted offering - insurance scope, terms, chose all interesting options and is now ready to finalize transaction (pay for the insurance).

At this stage we:
1. Gather additional data for after-sales care and operations like sending confirmation e-mail or render "thank you" page. We also pass these information to core policy system, TIA.
2. Handle the payment.
3. Print insurance policy documents.
    - We use existing, external module for this.
4. Gather agreements for marketing contact.

---

# Tech stack
- Java 21
- Angular 18
- Spring Boot 3.4
