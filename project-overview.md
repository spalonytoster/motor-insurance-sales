# MOTOR Insurance Sales

# Project Overview
This is an IT project for insurance company that is under digital transformation.
Aim of this project is to rewrite existing legacy MOTOR insurance calculator (sales tool) to new tech stack. Product is now long time on the market and with deep knowledge of this business due to its' history might be able to redesign system's architecture for a lot more alignment with business needs and company's strategy.

## System users
Users of this system are insurance agents, insurer's call center internal employees and direct clients.
Every type of user has certain needs but we also decide what functionalities or what data are available to which user types. For example only call center employee is able to see into claims history of customer, his driving records and customer segmentation insights. Since insurance agents are not insurer's direct employees hence they are not allowed to get such insights (otherwise they could potentially sell those data to 3rd parties). Customer is the most limited in this matter as customers are not regulated by any means compared to insurance agents.

## Business requirements
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
Focused on clear module boundaries that will assure consistent long-term development velocity and ability to scale independently if this product brings more revenue to the table. 



---

# Tech stack
- Java 21
- Angular 18
- Spring Boot 3.4
