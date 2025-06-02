# Insurance Sales Calculator - Architecture Documentation

## Table of Contents

1. **Technical Overview**
    - 1.1 System Purpose & Scope
    - 1.2 Key Architectural Drivers (Essential List)
    - 1.3 Solution Architecture Summary
    - 1.4 Technology Stack & Key Patterns
    - 1.5 Critical Design Decisions

2. **Business Context & Requirements Analysis**
    - 2.1 Sales Process Complexity Analysis
    - 2.2 Omnichannel Requirements Deep Dive
    - 2.3 Long-Running Workflow Requirements (7-day processes)
    - 2.4 Concurrency & Multi-Channel Access Patterns
    - 2.5 Integration Constraints (TIA/Earnix Dependencies)
    - 2.6 Performance & Scalability Requirements
    - 2.7 Regulatory & Compliance Considerations

3. **Architecture Strategy & Design Decisions**
    - 3.1 Architectural Principles & Trade-offs
    - 3.2 Pattern Selection Rationale (Command/Event, State Machine)
    - 3.3 Monolith vs Microservices Decision
    - 3.4 Technology Stack Decisions & Justification
    - 3.5 Alternative Approaches Considered & Rejected

4. **System Architecture Overview**
    - 4.1 High-Level Component Diagram
    - 4.2 Package Structure & Boundaries
    - 4.3 Dependency Flow & Module Relationships
    - 4.4 Runtime Architecture & Process Flow
    - 4.5 Deployment Architecture

5. **Domain Module Design**
    - 5.1 **CustomerJourney Module (Orchestrator)**
        - 5.1.1 Responsibilities & Boundaries
        - 5.1.2 Workflow State Management
        - 5.1.3 Command Coordination Patterns
        - 5.1.4 Event Handling Strategy
    - 5.2 **DataProcurement Module**
        - 5.2.1 Business Capabilities & Domain Logic
        - 5.2.2 Command Interface Design
        - 5.2.3 Data Transformation Patterns
        - 5.2.4 Validation & Error Handling
    - 5.3 **Offering Module**
        - 5.3.1 Pricing Logic & Business Rules
        - 5.3.2 TIA/Earnix Integration Patterns
        - 5.3.3 Product Versioning Strategy
        - 5.3.4 Performance Optimization
    - 5.4 **Checkout Module**
        - 5.4.1 Payment Processing Architecture
        - 5.4.2 Policy Creation Workflow
        - 5.4.3 Idempotency & Safety Mechanisms
        - 5.4.4 Compliance & Security Implementation
    - 5.5 **Shared Components**
        - 5.5.1 Common Infrastructure
        - 5.5.2 TIA Integration Layer
        - 5.5.3 Utility Libraries

6. **Command-Driven Architecture Implementation**
    - 6.1 Command Pattern Design & Interfaces
    - 6.2 Command Processing Pipeline
    - 6.3 Input Validation & Context Propagation
    - 6.4 Error Handling & Recovery Strategies
    - 6.5 Command Audit & Traceability

7. **Event-Driven Coordination**
    - 7.1 Domain Event Design & Contracts
    - 7.2 Event Processing Pipeline (Spring Events)
    - 7.3 Event-to-Journey Correlation Strategy
    - 7.4 Event Ordering & Idempotency
    - 7.5 Failure Handling & Dead Letter Processing

8. **State Management & Persistence**
    - 8.1 Workflow State Design Patterns
    - 8.2 Entity Models & Relationships
    - 8.3 State Transition Logic & Validation
    - 8.4 Versioning & Backward Compatibility
    - 8.5 State Reset & Cascade Operations

9. **Transaction Management & Data Consistency**
    - 9.1 Transaction Boundary Design
    - 9.2 Consistency Patterns (Immediate vs Eventual)
    - 9.3 Rollback & Recovery Mechanisms
    - 9.4 Concurrency Control Implementation
    - 9.5 Performance vs Consistency Trade-offs

10. **External System Integration**
    - 10.1 TIA Integration Architecture
    - 10.2 Adapter Pattern Implementation
    - 10.3 Circuit Breaker & Resilience Patterns
    - 10.4 Data Mapping & Transformation
    - 10.5 Integration Testing Strategy

11. **Cross-Cutting Concerns**
    - 11.1 Security Architecture & Implementation
    - 11.2 Logging, Monitoring & Observability
    - 11.3 Performance Monitoring & Optimization
    - 11.4 Error Handling & User Experience
    - 11.5 Configuration Management

12. **Implementation Guidelines**
    - 12.1 Code Organization & Package Structure
    - 12.2 Spring Boot Configuration Patterns
    - 12.3 Testing Strategy (Unit, Integration, E2E)
    - 12.4 Development Workflow & Team Practices
    - 12.5 Code Quality & Standards

13. **Operational Concerns**
    - 13.1 Deployment Strategy & CI/CD
    - 13.2 Environment Configuration
    - 13.3 Database Migration & Versioning
    - 13.4 Monitoring & Alerting Setup
    - 13.5 Incident Response Procedures

14. **Evolution & Future Architecture**
    - 14.1 Scalability Planning & Bottleneck Analysis
    - 14.2 Cross-Product Reuse Strategy
    - 14.3 TIA Migration & Exit Strategy
    - 14.4 Technology Evolution Path
    - 14.5 Refactoring Roadmap

15. **Appendices**
    - 15.1 Architecture Decision Records (ADRs)
    - 15.2 Technical Glossary
    - 15.3 API Reference & Examples
    - 15.4 Troubleshooting Guide
    - 15.5 Performance Benchmarks

---

## 1. Technical Overview

### 1.1 System Purpose & Scope

**Hexagon-with-BFF** is a modern insurance sales platform that enables agents and call center staff to create, manage, and complete motor insurance quotations through an intuitive web interface. The system serves as the user-facing layer for insurance sales operations, providing sophisticated workflow management while integrating with existing enterprise systems.

**Primary Capabilities:**
- **Interactive Quotation Builder**: Multi-step process for gathering customer data, calculating pricing, and finalizing insurance policies
- **Omnichannel Sales Support**: Seamless transitions between agent desktop, call center, and potential future channels
- **Long-Running Workflow Management**: Resumable sales processes with persistent state management
- **Real-Time Pricing Integration**: Live pricing calculations through enterprise pricing engines
- **Policy Creation Pipeline**: End-to-end policy creation with payment processing and compliance validation

**System Boundaries:**
- **In Scope**: Sales experience, workflow orchestration, user interface, pricing presentation, payment processing coordination
- **Out of Scope**: Core policy management (TIA), pricing rule engine (Earnix), underwriting logic, claims processing
- **Integration Points**: TIA (Oracle-based policy management system), Earnix (tariff/pricing engine via TIA)

**Target Users:**
- Insurance sales agents (primary)
- Call center representatives (secondary)
- Internal sales support staff

### 1.2 Key Architectural Drivers (Essential List)

**Business Process Complexity:**
- 7-day resumable workflows with persistent intermediate states
- Cross-channel process continuation (agent → call center → online)
- Complex multi-step sales funnel with branching logic

**Concurrency & Data Consistency:**
- 2-hour concurrent access control between channels
- Product version locking for price guarantee compliance
- Optimistic concurrency handling for simultaneous modifications

**Integration Constraints:**
- Heavy dependency on TIA Oracle PL/SQL business logic
- Limited direct access to Earnix pricing engine (via TIA only)
- Real-time pricing calculation requirements with external system latency

**Team & Development Scalability:**
- 4+ developer team with domain-based ownership model
- Rapid feature development cycle for business requirement changes
- Cross-product reuse potential for future insurance product calculators

**Operational Requirements:**
- High availability during business hours (8AM-8PM)
- Sub-2-second response times for pricing calculations
- Audit trail requirements for regulatory compliance

### 1.3 Solution Architecture Summary

**Architectural Style**: **Modular Monolith** with domain-driven package boundaries
- Single Spring Boot application with clear internal module separation
- Command-driven architecture for module coordination
- Event-driven workflow progression using Spring Application Events

**Core Design Pattern**: **Workflow State Machine + Command Orchestration**
```
CustomerJourney (Orchestrator)
    ├── Commands → DataProcurement Module
    ├── Commands → Offering Module  
    ├── Commands → Checkout Module
    └── Events ← Domain Events from all modules
```

**Module Organization:**
- **CustomerJourney**: Workflow orchestrator managing state transitions and cross-module coordination
- **DataProcurement**: Customer data collection, validation, and enrichment logic
- **Offering**: Pricing calculation, product configuration, and quotation management
- **Checkout**: Payment processing, policy creation, and finalization workflows
- **Shared**: Common infrastructure, TIA integration, and utility components

**State Management Strategy:**
- Persistent workflow state with minimal inter-module data exposure
- JPA entities for workflow persistence with embedded value objects for module states
- Event sourcing for audit trail and debugging capabilities

**Integration Approach:**
- Adapter pattern for TIA integration with circuit breaker resilience
- Command pattern for internal module communication
- Repository pattern for data access with transaction boundary management

### 1.4 Technology Stack & Key Patterns

**Core Technology Stack:**
```yaml
Runtime Platform:
  - Java 21 (LTS)
  - Spring Boot 3.2.5
  - Spring Framework 6.x

Persistence & Data:
  - Spring Data JPA / Hibernate
  - Oracle Database (enterprise constraint)
  - HikariCP connection pooling
  - Flyway database migrations

Web & API:
  - Spring MVC (@RestController)
  - Spring Security (authentication/authorization)
  - Jackson JSON processing
  - OpenAPI 3 documentation

Integration:
  - Spring Integration (TIA connectivity)
  - Spring Retry (resilience patterns)
  - Oracle JDBC drivers
  - Apache HttpClient (external APIs)

Development & Build:
  - Gradle multi-module project
  - JUnit 5 + Mockito (testing)
  - Spring Boot Test (integration testing)
  - ArchUnit (architecture testing)

Monitoring & Operations:
  - Spring Boot Actuator
  - Micrometer metrics
  - Logback structured logging
```

**Key Design Patterns:**
- **Command Pattern**: Module interface design and request processing
- **Domain Events**: Workflow coordination and state change notifications
- **State Machine**: Workflow step management and transition validation
- **Adapter Pattern**: External system integration and data transformation
- **Repository Pattern**: Data access abstraction and transaction management
- **Circuit Breaker**: TIA integration resilience and failure handling

**Architectural Patterns:**
- **Hexagonal Architecture**: Clear separation between domain logic and infrastructure
- **CQRS (Light)**: Command/query separation for complex workflow operations
- **Event-Driven Architecture**: Loose coupling between workflow steps
- **Domain-Driven Design**: Package organization and bounded context modeling

### 1.5 Critical Design Decisions

**1. Monolith vs Microservices Decision**
```
Decision: Single Spring Boot application with package-based modules
Rationale: Team size (4 developers), shared database constraints, deployment simplicity
Trade-off: Accepts coupling for operational simplicity and development velocity
```

**2. Command-Driven vs Direct Module Dependencies**
```
Decision: Command interfaces for inter-module communication
Rationale: Testability, clear boundaries, future extraction possibilities  
Trade-off: Additional abstraction layer for cleaner architecture
```

**3. Synchronous vs Asynchronous Event Processing**
```
Decision: Synchronous Spring Application Events within transactions
Rationale: ACID consistency requirements, simpler error handling
Trade-off: Processing latency for guaranteed consistency
Evolution Path: Async events with outbox pattern if needed
```

**4. State Persistence Strategy**
```
Decision: JPA entities with embedded value objects for module states
Rationale: Relational database constraints, ACID transactions, query capabilities
Trade-off: Object-relational mapping complexity for persistence simplicity
```

**5. TIA Integration Approach**
```
Decision: Direct Oracle database integration with adapter pattern
Rationale: TIA API limitations, performance requirements, transaction control
Trade-off: Database coupling for operational control and performance
```

**6. Transaction Boundary Design**
```
Decision: Single transaction for command execution + immediate state updates
Rationale: Data consistency guarantees, simpler error recovery
Evolution Path: Saga pattern for complex workflows if needed
Trade-off: Potential rollback of valid work for consistency guarantees
```

**7. Team Organization Strategy**
```
Decision: Module-based ownership with tech lead coordination
Rationale: Domain expertise development, parallel development capability
Trade-off: Knowledge silos risk for development velocity
```

## 2. Business Context & Requirements Analysis

### 2.1 Sales Process Complexity Analysis

**Business Context:**
Motor insurance sales involve a complex multi-phase process that extends far beyond simple product catalog shopping. The sales journey requires sophisticated data gathering, risk assessment, pricing calculation, coverage customization, and regulatory compliance validation. Unlike e-commerce transactions, insurance sales involve significant consultation, education, and negotiation phases.

**Current Sales Process Phases:**
1. **Customer Identification & Data Collection**: Personal details, vehicle information, driving history, coverage preferences
2. **Risk Assessment & Data Enrichment**: Credit checks, claims history validation, vehicle valuation, geographic risk factors
3. **Product Presentation & Pricing**: Multiple coverage options, discount calculations, competitive comparisons
4. **Coverage Customization**: Deductible selection, optional coverage additions, policy term configuration
5. **Final Validation & Underwriting**: Compliance checks, fraud detection, final risk assessment
6. **Payment Processing & Policy Creation**: Payment validation, document generation, policy activation

**Technical Implications:**
- **Complex State Management**: Each phase produces data required by subsequent phases, creating intricate state dependencies
- **Branching Logic Requirements**: Different customer profiles and risk factors lead to different process paths
- **Integration Complexity**: Each phase typically requires multiple external system calls for validation and enrichment
- **Error Recovery Complexity**: Failed validations may require returning to previous phases with modified data

**Architectural Drivers:**
- Need for clear phase boundaries with well-defined input/output contracts
- Requirement for state persistence between phases to enable interruption and resumption
- Integration patterns that can handle complex data transformations and validations
- Error handling strategies that can gracefully handle phase-specific failures

### 2.2 Omnichannel Requirements Deep Dive

**Business Context:**
Modern insurance customers expect seamless experiences across multiple touchpoints. A customer might begin a quotation process on a company website, continue it during a phone call with customer service, and finalize it during an in-person meeting with an agent. Each channel has different capabilities, user interfaces, and business rules, but customers expect consistent data and pricing regardless of channel.

**Channel-Specific Characteristics:**
- **Agent Desktop**: Full feature access, manual override capabilities, complex underwriting tools, relationship management integration
- **Call Center**: Standardized scripts, compliance monitoring, limited override authority, call recording integration
- **Online Portal** (Future): Self-service capabilities, simplified UI, automated validation, limited product options
- **Mobile App** (Future): Quick quotes, photo-based data entry, location-based services

**Technical Challenges:**
- **Data Consistency**: Same customer data must be available and consistent across all channels
- **Business Rule Variations**: Different channels may have different validation rules, pricing rules, or available products
- **Security Context**: Each channel has different authentication mechanisms and authorization levels
- **Integration Complexity**: Each channel may integrate with different backend systems or use different protocols

**Architectural Implications:**
- **Channel-Agnostic Domain Logic**: Core business logic must be separated from channel-specific presentation and validation
- **Flexible Authorization Model**: Role-based permissions that can accommodate different channel access patterns
- **Consistent API Layer**: Unified command interfaces that can serve multiple channel types
- **Configuration-Driven Behavior**: Channel-specific rules and validations that can be configured rather than hard-coded

### 2.3 Long-Running Workflow Requirements (7-day processes)

**Business Context:**
Insurance sales processes frequently span multiple days or weeks due to customer deliberation, required documentation collection, external validations, and approval processes. Customers may need time to compare options, consult with family members, or gather required documents. Sales agents often work on multiple quotations simultaneously and need to track process state across extended timeframes.

**Specific Long-Running Scenarios:**
- **Document Collection**: Customers may need several days to provide driving records, vehicle registration, or proof of residence
- **Comparative Shopping**: Customers often request quotes from multiple insurers and take time to compare options
- **Approval Processes**: High-risk customers or expensive policies may require underwriter approval, adding days to the process
- **Payment Arrangement**: Commercial customers may need time to arrange payment methods or get approval for expenditures

**Technical Requirements:**
- **Persistent State Management**: All intermediate process state must be reliably stored and retrievable after extended periods
- **State Expiration Handling**: Clear policies for when incomplete processes should be expired or archived
- **Data Consistency Over Time**: Handle scenarios where external data (like credit scores or vehicle values) may change during the process
- **Process Resume Capability**: Users must be able to continue exactly where they left off, regardless of time elapsed

**Architectural Considerations:**
- **Robust Persistence Strategy**: Database design that can efficiently store and retrieve complex workflow state
- **Version Compatibility**: Handle cases where application versions change during long-running processes
- **External Data Refresh**: Mechanisms to determine when external data should be refreshed vs preserved for consistency
- **Audit Trail Requirements**: Complete tracking of all changes and decisions throughout the extended process

### 2.4 Concurrency & Multi-Channel Access Patterns

**Business Context:**
Insurance sales often involve multiple stakeholders working on the same customer case simultaneously or in rapid succession. A customer might call customer service while an agent is actively working on their quotation, or multiple agents might need to collaborate on complex commercial cases. The business has established a 2-hour rule to prevent conflicts while allowing reasonable collaboration timeframes.

**Concurrency Scenarios:**
- **Agent Handoff**: Day-shift agent starts quotation, evening-shift agent needs to complete it
- **Customer Service Escalation**: Customer calls with questions while agent is preparing quotation variants
- **Supervisory Review**: Manager needs to review and approve agent's work without blocking continued progress
- **Team Collaboration**: Complex cases requiring input from multiple specialists (underwriting, claims, product experts)

**Business Rules:**
- **2-Hour Exclusive Access**: Once a user modifies a quotation, other users from different channels cannot modify it for 2 hours
- **Same-User Exception**: The same user can continue working regardless of channel (agent desktop → call center system)
- **View-Only Access**: Other users can view quotations but cannot modify them during exclusion period
- **Override Capabilities**: Supervisors can override exclusion rules in exceptional circumstances

**Technical Implementation Requirements:**
- **Optimistic Locking**: Prevent lost updates when multiple users attempt simultaneous modifications
- **Time-Based Access Control**: Automatic release of exclusive access after specified timeframes
- **User Session Tracking**: Track user identity across different channels and systems
- **Conflict Resolution**: Clear error messages and resolution paths when access conflicts occur

### 2.5 Integration Constraints (TIA/Earnix Dependencies)

**Business Context:**
The system operates within a complex enterprise architecture where core business logic resides in external systems that cannot be easily modified or replaced. TIA (Oracle-based policy management system) contains the authoritative customer database, policy templates, and underwriting rules. Earnix contains sophisticated pricing algorithms, risk models, and regulatory rate filings. These systems represent millions of dollars in investment and years of regulatory approval.

**TIA System Characteristics:**
- **Oracle PL/SQL Business Logic**: Core underwriting rules, policy validations, and data transformations implemented in stored procedures
- **Authoritative Data Source**: Customer information, policy history, claims data, agent information
- **Complex Data Models**: Highly normalized database structure optimized for policy management rather than sales processes
- **Performance Characteristics**: Optimized for batch processing and complex reporting rather than real-time user interactions
- **Integration Limitations**: Limited API surface, primarily SQL-based integration patterns

**Earnix System Characteristics:**
- **Sophisticated Rating Engine**: Complex algorithms for risk assessment and pricing calculation
- **Regulatory Compliance**: Rate filings, territory definitions, and regulatory rule enforcement
- **Limited Direct Access**: Integration only available through TIA system calls
- **Batch-Oriented Design**: Optimized for periodic rate updates rather than real-time individual calculations

**Technical Constraints:**
- **Performance Bottlenecks**: External system calls can introduce significant latency into user workflows
- **Data Transformation Complexity**: Mapping between user-friendly interfaces and complex backend data structures
- **Error Handling Challenges**: Limited error information from external systems makes debugging difficult
- **Transaction Boundary Limitations**: Cannot include external system calls in local database transactions

**Architectural Adaptation Strategies:**
- **Adapter Pattern Implementation**: Isolate external system complexity behind clean internal interfaces
- **Circuit Breaker Patterns**: Prevent cascading failures when external systems are unavailable
- **Caching Strategies**: Cache frequently accessed data to reduce external system calls
- **Asynchronous Processing**: Decouple user interactions from slow external system operations where possible

### 2.6 Performance & Scalability Requirements

**Business Context:**
The sales platform operates during concentrated business hours (8AM-8PM) with peak usage periods during lunch hours and early evening when customers are most likely to shop for insurance. Sales agents are highly productive workers whose time directly correlates to revenue generation, making system responsiveness a critical business factor.

**Performance Requirements:**
- **Interactive Response Times**: Sub-2-second response for all user interactions during normal system load
- **Pricing Calculation Performance**: Real-time pricing updates as customers modify coverage options
- **Concurrent User Support**: Support for 50+ simultaneous users during peak business hours
- **Data Loading Performance**: Fast loading of customer history and complex quotation data

**Scalability Considerations:**
- **Geographic Distribution**: Potential expansion to multiple regions with varying peak hours
- **Product Line Expansion**: Architecture must support additional insurance products (home, commercial, life)
- **User Base Growth**: System must accommodate 2-3x current user base over next 3 years
- **Data Volume Growth**: Customer and policy data grows continuously and never shrinks

**Technical Performance Targets:**
- **Application Response Time**: 95th percentile under 1.5 seconds for all operations
- **Database Query Performance**: Complex reporting queries under 5 seconds
- **External Integration Performance**: TIA integration calls under 3 seconds for simple operations
- **Memory Usage**: Stable memory usage under high concurrent load
- **System Availability**: 99.5% availability during business hours

### 2.7 Regulatory & Compliance Considerations

**Business Context:**
Insurance is a heavily regulated industry with strict requirements for data handling, pricing transparency, policy documentation, and consumer protection. Regulatory requirements vary by jurisdiction and change frequently, requiring systems that can adapt to new compliance requirements without major architectural changes.

**Key Regulatory Requirements:**
- **Data Privacy**: GDPR-style requirements for personal data handling, consent management, and data retention
- **Pricing Transparency**: Detailed documentation of how prices are calculated, including all discounts and surcharges
- **Policy Documentation**: Complete audit trail of all policy terms, conditions, and customer agreements
- **Rate Filing Compliance**: All pricing must comply with approved rate filings in each jurisdiction
- **Consumer Protection**: Clear disclosure of terms, cooling-off periods, and cancellation rights

**Audit & Compliance Requirements:**
- **Complete Transaction Logging**: Every system interaction must be logged with timestamp, user, and business context
- **Data Retention Policies**: Customer data must be retained for specified periods and then securely disposed
- **Access Control Auditing**: Complete tracking of who accessed what customer data and when
- **Change Management**: All system changes must be documented and approved through controlled processes

**Technical Compliance Implications:**
- **Audit Trail Architecture**: Comprehensive logging and event sourcing for regulatory reporting
- **Data Encryption**: Encryption at rest and in transit for all personally identifiable information
- **Access Control**: Role-based security with fine-grained permissions and regular access reviews
- **Configuration Management**: Externalized business rules that can be updated without code changes
- **Disaster Recovery**: Business continuity planning with defined recovery time objectives

## 3. Architecture Strategy & Design Decisions

### 3.1 Architectural Principles & Trade-offs

**Principle 1: Pragmatism Over Purity**
```
Decision: Choose simpler solutions that solve immediate business problems effectively
Rationale: 4-developer team with aggressive timeline needs rapid value delivery
Trade-off: Accept some technical debt for faster feature delivery and business validation
Implementation: Monolith over microservices, JPA over event sourcing, SQL over NoSQL
```

**Principle 2: Evolutionary Architecture**
```
Decision: Design for easy modification and extraction rather than perfect initial boundaries
Rationale: Business domain understanding will evolve, TIA exit strategy is long-term
Trade-off: Some over-abstraction today for flexibility tomorrow
Implementation: Command interfaces, event boundaries, clean package separation
```

**Principle 3: Domain-Driven Boundaries**
```
Decision: Organize code around business capabilities rather than technical layers
Rationale: Insurance sales has distinct business phases with different stakeholders
Trade-off: Some code duplication across modules for better cohesion
Implementation: Package-per-domain with internal clean architecture
```

**Principle 4: Consistency Over Performance**
```
Decision: Prioritize data consistency and simplicity over maximum performance
Rationale: Financial transactions require ACID guarantees, debug-ability is critical
Trade-off: Accept higher latency for guaranteed consistency and simpler error handling
Implementation: Synchronous processing, single database transactions, immediate consistency
```

**Principle 5: Integration Isolation**
```
Decision: Isolate external system complexity behind clean internal interfaces
Rationale: TIA/Earnix systems are complex, slow, and outside our control
Trade-off: Additional abstraction layers for protection against external changes
Implementation: Adapter pattern, circuit breakers, retry mechanisms
```

**Principle 6: Observable Architecture**
```
Decision: Build comprehensive logging and monitoring from day one
Rationale: Complex workflows and external integrations make debugging challenging
Trade-off: Additional development effort for operational excellence
Implementation: Structured logging, metrics, event sourcing for audit trail
```

### 3.2 Pattern Selection Rationale (Command/Event, State Machine)

**Command Pattern Selection**

**Problem Context:**
Need for clear module boundaries that allow independent development while maintaining testability and future extraction possibilities. Traditional layered architecture creates tight coupling between modules.

**Decision Rationale:**
```java
// Instead of direct dependencies:
public class CustomerJourneyService {
    @Autowired
    private DataProcurementService dataProcurementService; // Tight coupling
    
    public void processCustomer(CustomerId id) {
        var result = dataProcurementService.process(id); // Direct call
    }
}

// Use command interfaces:
public class CustomerJourneyService {
    private final DataProcurementCommands dataProcurementCommands; // Interface dependency
    
    public void processCustomer(CustomerId id) {
        var result = dataProcurementCommands.processCustomerData(context); // Command call
    }
}
```

**Benefits Realized:**
- **Testability**: Easy to mock command interfaces for unit testing
- **Boundary Enforcement**: Clear contracts between modules prevent accidental coupling
- **Future Extraction**: Command interfaces can become REST APIs if modules are extracted
- **Team Development**: Teams can develop against interfaces before implementations are complete

**Trade-offs Accepted:**
- **Additional Abstraction**: Extra interface layer adds cognitive overhead
- **Indirection**: More complex call stacks make debugging slightly harder
- **Over-Engineering Risk**: May be unnecessary complexity for current team size

**Event-Driven Coordination Selection**

**Problem Context:**
Need for loose coupling between workflow steps while maintaining data consistency and clear process flow. Traditional procedural workflows create tight coupling and make testing difficult.

**Decision Rationale:**
```java
// Instead of procedural workflow:
@Transactional
public void processCustomerData(CustomerId id) {
    var procurement = dataProcurementService.process(id);
    var offering = offeringService.calculate(procurement); // Tight coupling
    var checkout = checkoutService.prepare(offering);      // Sequential dependency
}

// Use event-driven coordination:
@EventListener
public void onDataProcurementComplete(DataProcurementCompleteEvent event) {
    // Loose coupling through events
    offeringCommands.startOffering(new OfferingContext(event.getDataProcurementId()));
}
```

**Benefits Realized:**
- **Loose Coupling**: Modules don't need to know about each other directly
- **Extensibility**: Easy to add new workflow steps or parallel processing
- **Testability**: Can test each event handler independently
- **Audit Trail**: Events provide natural audit log of workflow progression

**Trade-offs Accepted:**
- **Complexity**: Event flow is harder to follow than sequential code
- **Debugging Difficulty**: Scattered event handlers make troubleshooting more complex
- **Performance Overhead**: Event publishing and handling adds latency
- **Transaction Boundaries**: Need careful consideration of when events fire vs when transactions commit

**State Machine Pattern Selection**

**Problem Context:**
Insurance sales workflows have complex state transitions, validation rules, and business logic that varies by current state. Traditional if-else chains become unmaintainable and error-prone.

**Decision Rationale:**
```java
// Instead of complex conditional logic:
public void updateCustomerData(CustomerJourneyId id, CustomerData data) {
    var journey = repository.findById(id);
    if (journey.getStep() == OFFERING && journey.isOfferingComplete()) {
        // Reset offering and checkout - complex logic scattered
        journey.resetOffering();
        journey.resetCheckout();
        // ... more conditional logic
    }
}

// Use explicit state machine:
public enum WorkflowStep {
    DATA_PROCUREMENT {
        @Override
        public Set<WorkflowStep> allowedTransitions() {
            return Set.of(OFFERING, DATA_PROCUREMENT);
        }
        
        @Override
        public void onDataChange(CustomerJourney journey) {
            // State-specific behavior
        }
    },
    OFFERING { /* ... */ },
    CHECKOUT { /* ... */ }
}
```

**Benefits Realized:**
- **Clear State Transitions**: Explicit definition of valid state changes
- **Business Logic Organization**: State-specific behavior is co-located
- **Validation**: Automatic prevention of invalid state transitions
- **Documentation**: State machine serves as living documentation of business process

**Trade-offs Accepted:**
- **Learning Curve**: Team needs to understand state machine concepts
- **Overhead**: State machine infrastructure for relatively simple workflows
- **Rigidity**: May make some legitimate state transitions difficult to implement

### 3.3 Monolith vs Microservices Decision

**Decision Context:**
Modern architecture discussions often default to microservices, but project constraints suggested careful evaluation was needed.

**Evaluation Criteria:**
```
Team Size: 4 developers (including tech lead)
Domain Maturity: New greenfield project with evolving requirements
Data Consistency: Financial transactions requiring ACID guarantees
Deployment Complexity: Limited DevOps resources and expertise
Integration Constraints: Shared Oracle database with TIA system
Performance Requirements: Sub-2-second response times
```

**Microservices Approach Evaluation:**
```yaml
Potential Benefits:
  - Independent deployment of modules
  - Technology diversity for different problems
  - Team autonomy and ownership
  - Scalability of individual components

Identified Problems:
  - Distributed transaction complexity with financial data
  - Network latency between services impacting user experience
  - Deployment orchestration complexity with limited DevOps resources
  - Debugging distributed systems with small team
  - Shared database creates deployment coupling anyway
  - Team size too small for meaningful service ownership
```

**Monolith with Modular Design Decision:**
```yaml
Chosen Benefits:
  - Single deployment unit reduces operational complexity
  - ACID transactions across entire workflow
  - Simplified debugging and development workflow
  - Single codebase for easier refactoring as domain evolves
  - Package-based modules provide boundaries without distribution costs

Accepted Trade-offs:
  - Single point of failure for entire application
  - Shared runtime means one slow module affects all
  - Technology choices impact entire application
  - Scaling requires scaling entire application
```

**Evolution Strategy:**
```java
// Current: Package-based modules
com.company.insurance.dataprocurement
com.company.insurance.offering
com.company.insurance.checkout

// Future: Extract modules if needed
insurance-data-procurement-service
insurance-offering-service  
insurance-checkout-service

// Migration path: Command interfaces become REST APIs
@FeignClient("data-procurement-service")
public interface DataProcurementCommands {
    @PostMapping("/procurement/process")
    DataProcurementResult processCustomerData(@RequestBody DataProcurementContext context);
}
```

### 3.4 Technology Stack Decisions & Justification

**Java 21 LTS Selection**
```
Decision: Java 21 (latest LTS) over Java 17 or Java 8
Business Driver: Long-term platform stability with modern language features
Technical Benefits:
  - Virtual threads for improved concurrency (future optimization)
  - Pattern matching and records for cleaner domain modeling
  - Text blocks and improved string handling for SQL queries
  - 6+ years of LTS support for platform stability
Trade-off: Newer runtime may have fewer production battle-tested examples
Risk Mitigation: Gradual adoption of new features, fallback to proven patterns
```

**Spring Boot 3.2.5 Selection**
```
Decision: Spring Boot 3.x over Spring Boot 2.x or alternative frameworks
Business Driver: Ecosystem maturity and team familiarity
Technical Benefits:
  - Native compilation support (future performance optimization)
  - Improved observability with Micrometer integration
  - Jakarta EE migration provides future-proofing
  - Extensive integration ecosystem for Oracle/enterprise systems
Trade-off: Spring complexity can lead to "magic" behavior difficult to debug
Risk Mitigation: Clear configuration patterns, extensive testing, team training
```

**Oracle Database Constraint**
```
Decision: Work within Oracle constraint rather than fight it
Business Driver: TIA integration requirements and enterprise data governance
Technical Benefits:
  - ACID transactions with strong consistency guarantees
  - Mature tooling and enterprise support
  - Team familiarity and operational expertise
  - Advanced analytics and reporting capabilities
Trade-off: Vendor lock-in, licensing costs, less optimal for microservices
Alternative Considered: PostgreSQL with Oracle gateway (rejected due to complexity)
```

**JPA/Hibernate ORM Selection**
```
Decision: JPA/Hibernate over JDBC or alternative persistence frameworks
Business Driver: Developer productivity and maintainability
Technical Benefits:
  - Type-safe query construction with Criteria API
  - Automatic schema generation and migration support
  - Caching and lazy loading for performance optimization
  - Rich relationship mapping for complex domain models
Trade-off: N+1 query problems, complex debugging, impedance mismatch issues
Alternative Considered: JOOQ (rejected due to learning curve and code generation)
```

**Gradle Multi-Module Selection**
```
Decision: Gradle over Maven for build system
Business Driver: Flexibility for future module extraction
Technical Benefits:
  - Flexible dependency management for module boundaries
  - Performance advantages with build caching and incremental compilation
  - Groovy/Kotlin DSL for complex build logic
  - Better support for composite builds and multi-module projects
Trade-off: More complex than Maven, requires Gradle expertise
Alternative Considered: Maven (rejected due to verbosity and limited flexibility)
```

### 3.5 Alternative Approaches Considered & Rejected

**Alternative 1: Event Sourcing Architecture**
```yaml
Approach: Store all changes as immutable events, rebuild state from event stream
Potential Benefits:
  - Perfect audit trail for regulatory compliance
  - Time-travel debugging capabilities
  - Natural support for workflow replay and testing
  - Eventual consistency models

Rejection Reasons:
  - Complexity overhead too high for 4-developer team
  - Oracle database not optimized for event streaming patterns
  - Query complexity for business reporting requirements
  - Team expertise gap in event sourcing patterns
  - Business stakeholder comfort with traditional CRUD operations

Implementation Complexity Example:
// Event sourcing complexity
public class CustomerJourney {
    public static CustomerJourney fromEvents(List<DomainEvent> events) {
        // Complex event replay logic
        var journey = new CustomerJourney();
        events.forEach(journey::apply);
        return journey;
    }
}

// vs. Simple state approach
@Entity
public class CustomerJourney {
    // Direct state representation - easier to understand and query
}
```

**Alternative 2: CQRS with Separate Read/Write Models**
```yaml
Approach: Separate command and query models with different persistence strategies
Potential Benefits:
  - Optimized read models for complex reporting
  - Independent scaling of read and write operations
  - Flexibility to use different persistence technologies

Rejection Reasons:
  - Added complexity for marginal benefits at current scale
  - Eventual consistency challenges with financial data
  - Additional infrastructure overhead (multiple databases)
  - Team expertise gap in managing distributed data consistency

Complexity Example:
// CQRS approach
@Component
public class CustomerJourneyCommandHandler {
    // Complex command handling with event publishing
}

@Component  
public class CustomerJourneyQueryHandler {
    // Separate query model with eventual consistency concerns
}

// vs. Traditional approach
@Service
public class CustomerJourneyService {
    // Simple unified service with immediate consistency
}
```

**Alternative 3: Microservices with Saga Pattern**
```yaml
Approach: Distributed services with saga orchestration for workflow management
Potential Benefits:
  - Independent service deployment and scaling
  - Technology diversity for different problem domains
  - Clear service boundaries and ownership

Rejection Reasons:
  - Distributed transaction complexity outweighs benefits
  - Network latency impacts user experience requirements
  - Operational complexity too high for current team
  - Debugging distributed workflows significantly more complex
  - Shared database creates coupling despite service separation

Implementation Complexity:
// Saga coordination complexity
@Component
public class InsuranceSalesOrchestrator {
    public void processApplication(SalesApplication app) {
        // Complex distributed transaction coordination
        // Compensation logic for partial failures
        // Network timeout handling
        // Service discovery and health checking
    }
}

// vs. Monolith simplicity
@Transactional
public void processApplication(SalesApplication app) {
    // Simple transaction boundary, automatic rollback
}
```

**Alternative 4: Domain-Driven Design with Aggregate Pattern**
```yaml
Approach: Rich domain models with aggregate roots and domain services
Potential Benefits:
  - Business logic encapsulation in domain objects
  - Clear aggregate boundaries and consistency rules
  - Domain expert collaboration through ubiquitous language

Rejection Reasons:
  - Domain complexity constrained by TIA business logic ownership
  - Oracle database structure doesn't align with aggregate boundaries
  - Team expertise gap in advanced DDD patterns
  - Risk of over-engineering for current domain maturity level

Complexity Example:
// Full DDD approach
public class CustomerJourney extends AggregateRoot {
    private List<DomainEvent> uncommittedEvents = new ArrayList<>();
    
    public void startDataProcurement(DataProcurementCommand command) {
        // Complex domain logic with invariant enforcement
        // Event generation and consistency rules
    }
    
    public List<DomainEvent> getUncommittedEvents() { /* ... */ }
}

// vs. Simplified domain approach  
@Entity
public class CustomerJourney {
    public void updateState(WorkflowStep newStep) {
        // Simple state transitions with business validation
    }
}
```

**Alternative 5: NoSQL Document Database**
```yaml
Approach: Use MongoDB or similar for flexible schema and JSON document storage
Potential Benefits:
  - Schema flexibility for evolving workflow requirements
  - Natural JSON representation for web APIs
  - Horizontal scaling capabilities
  - Better alignment with microservices architecture

Rejection Reasons:
  - TIA integration requires Oracle database connectivity
  - Loss of ACID transaction guarantees for financial data
  - Team expertise gap in NoSQL operations and modeling
  - Enterprise governance and compliance requirements favor relational databases
  - Complex reporting requirements better suited to SQL

Data Model Complexity:
// NoSQL complexity for relational data
{
  "customerJourney": {
    "dataProcurement": { /* nested object */ },
    "offering": { /* nested object */ },
    "relationships": [ /* complex denormalization */ ]
  }
}

// vs. Relational clarity
@Entity public class CustomerJourney { /* ... */ }
@Entity public class DataProcurement { /* ... */ }
@Entity public class Offering { /* ... */ }
```

**Key Decision Framework Applied:**
For each alternative, the decision framework prioritized:
1. **Team Capability**: Can our 4-developer team effectively implement and maintain this?
2. **Business Value**: Does this solve actual business problems or just theoretical architecture problems?
3. **Risk Management**: What are the failure modes and how easily can we recover?
4. **Evolution Path**: Does this help or hinder future architectural evolution?
5. **Operational Simplicity**: Can we deploy, monitor, and debug this effectively?

The chosen architecture represents the optimal balance of these factors for the current project context while maintaining clear paths for future evolution as requirements and team capabilities grow.

## 4. System Architecture Overview

### 4.1 High-Level Component Diagram

**System Context View**
```
┌─────────────────────────────────────────────────────────────────┐
│                    Insurance Sales Platform                     │
│                      (hexagon-with-bff)                        │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │   Web Layer     │  │  Application    │  │ Infrastructure  │ │
│  │                 │  │     Layer       │  │     Layer       │ │
│  │ • Controllers   │  │ • Command Proc. │  │ • TIA Adapters  │ │
│  │ • DTOs          │  │ • Event Coord.  │  │ • Database      │ │
│  │ • Validation    │  │ • Workflow Mgmt │  │ • External APIs │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                                   │
            ┌──────────────────────┼──────────────────────┐
            │                      │                      │
    ┌───────▼───────┐    ┌────────▼────────┐    ┌───────▼────────┐
    │  Agent Desktop │    │   TIA System    │    │ Earnix System  │
    │   (Angular)    │    │   (Oracle)      │    │ (via TIA)      │
    │                │    │                 │    │                │
    │ • Sales UI     │    │ • Policy Mgmt   │    │ • Pricing      │
    │ • Customer Mgmt│    │ • Customer DB   │    │ • Rating       │
    │ • Workflow     │    │ • Underwriting  │    │ • Rate Filings │
    └────────────────┘    └─────────────────┘    └────────────────┘
```

**Domain Module Architecture**
```
┌─────────────────────────────────────────────────────────────────┐
│                    CustomerJourney Module                       │
│                      (Orchestrator)                            │
├─────────────────────────────────────────────────────────────────┤
│  • Workflow State Management                                   │
│  • Command Coordination                                        │
│  • Event Processing                                            │
│  • Cross-Module Integration                                    │
└─────────────────┬───────────────┬───────────────┬───────────────┘
                  │               │               │
        ┌─────────▼─────────┐ ┌───▼─────┐ ┌──────▼──────┐
        │  DataProcurement  │ │Offering │ │  Checkout   │
        │     Module        │ │ Module  │ │   Module    │
        ├───────────────────┤ ├─────────┤ ├─────────────┤
        │ • Data Collection │ │• Pricing│ │• Payment    │
        │ • Validation      │ │• Product│ │• Policy     │
        │ • Enrichment      │ │• Rules  │ │• Compliance │
        │ • TIA Integration │ │• TIA    │ │• TIA        │
        └───────────────────┘ └─────────┘ └─────────────┘
                  │               │               │
                  └───────────────┼───────────────┘
                                  │
                      ┌───────────▼───────────┐
                      │    Shared Module      │
                      ├───────────────────────┤
                      │ • TIA Integration     │
                      │ • Common Utilities    │
                      │ • Configuration       │
                      │ • Security            │
                      └───────────────────────┘
```

**Component Interaction Patterns**
```
Web Layer                Application Layer              Infrastructure
┌──────────┐    HTTP     ┌─────────────────┐   Commands  ┌──────────────┐
│Controller├─────────────►│CustomerJourney  ├─────────────►│TIA Adapter   │
│          │             │Service          │             │              │
│          │             │                 │             │              │
│          │    Events   │                 │   Events    │              │
│          │◄────────────┤Event Listeners  │◄────────────┤Domain Events │
└──────────┘             └─────────────────┘             └──────────────┘
                                   │
                            Commands│
                                   ▼
                         ┌─────────────────┐
                         │Module Commands  │
                         │• DataProcurement│
                         │• Offering       │
                         │• Checkout       │
                         └─────────────────┘
```

### 4.2 Package Structure & Boundaries

**Root Package Organization**
```
com.company.insurance/
├── customerjourney/           # Workflow orchestration module
│   ├── api/                  # REST controllers and DTOs
│   ├── application/          # Command coordination and event handling
│   ├── domain/              # Workflow entities and state management
│   └── infrastructure/      # Persistence and external integrations
├── dataprocurement/          # Customer data gathering module
│   ├── api/                 # Command interfaces and DTOs
│   ├── application/         # Data processing and validation services
│   ├── domain/              # Data procurement entities and logic
│   └── infrastructure/      # TIA data adapters and repositories
├── offering/                 # Pricing and quotation module
│   ├── api/                 # Command interfaces and DTOs
│   ├── application/         # Pricing calculation and product services
│   ├── domain/              # Offering entities and business rules
│   └── infrastructure/      # TIA pricing adapters and repositories
├── checkout/                 # Payment and policy creation module
│   ├── api/                 # Command interfaces and DTOs
│   ├── application/         # Payment and policy services
│   ├── domain/              # Checkout entities and workflows
│   └── infrastructure/      # Payment gateways and TIA policy adapters
└── shared/                   # Common infrastructure and utilities
    ├── domain/              # Shared value objects and base classes
    ├── infrastructure/      # Common TIA integration and utilities
    ├── security/            # Authentication and authorization
    └── configuration/       # Spring configuration and properties
```

**Module Boundary Definitions**

**CustomerJourney Module Boundaries:**
```java
// Public API - What other modules can access
public interface CustomerJourneyQueryService {
    CustomerJourneyState findById(CustomerJourneyId id);
    List<CustomerJourneyState> findByCustomerId(CustomerId customerId);
    boolean canBeModifiedBy(CustomerJourneyId id, UserId userId);
}

// Package-private - Internal implementation details
package com.company.insurance.customerjourney.domain;
class CustomerJourneyRepository { /* ... */ }
class WorkflowStateManager { /* ... */ }
class EventCorrelationService { /* ... */ }

// Inbound Dependencies - What this module depends on
@Component
public class CustomerJourneyService {
    private final DataProcurementCommands dataProcurementCommands;
    private final OfferingCommands offeringCommands;
    private final CheckoutCommands checkoutCommands;
}
```

**DataProcurement Module Boundaries:**
```java
// Public Command API
public interface DataProcurementCommands {
    DataProcurementResult processCustomerData(DataProcurementContext context);
    DataProcurementResult updateCustomerData(DataProcurementId id, CustomerData data);
    void cancelDataProcurement(DataProcurementId id);
}

// Public Query API  
public interface DataProcurementQueryService {
    DataProcurementResult findById(DataProcurementId id);
    CustomerData getCustomerData(DataProcurementId id);
    ValidationResult validateData(CustomerData data);
}

// Published Events
public class DataProcurementCompletedEvent {
    private final CustomerJourneyId journeyId;
    private final DataProcurementId procurementId;
    private final DataProcurementStatus status;
    // No internal domain objects exposed
}

// Private Implementation
package com.company.insurance.dataprocurement.domain;
class DataProcurement { /* Rich domain model */ }
class CustomerDataValidator { /* Internal validation logic */ }
class TiaCustomerDataAdapter { /* External integration */ }
```

**Cross-Module Dependencies Policy:**
```yaml
Allowed Dependencies:
  - CustomerJourney → All other modules (orchestrator privilege)
  - Any module → Shared module (common utilities)
  - Modules → External libraries (Spring, JPA, etc.)

Forbidden Dependencies:
  - DataProcurement → Offering (horizontal dependency)
  - DataProcurement → Checkout (horizontal dependency)
  - Offering → DataProcurement (horizontal dependency)
  - Offering → Checkout (horizontal dependency)
  - Checkout → DataProcurement (horizontal dependency)
  - Checkout → Offering (horizontal dependency)

Communication Patterns:
  - Commands: CustomerJourney calls other modules via command interfaces
  - Events: Other modules publish events, CustomerJourney subscribes
  - Queries: CustomerJourney queries other modules via query interfaces
```

### 4.3 Dependency Flow & Module Relationships

**Dependency Architecture Diagram**
```
┌─────────────────────────────────────────────────────────────────┐
│                      Web Layer                                  │
│  ┌─────────────────────────────────────────────────────────────┤
│  │              CustomerJourney Controllers                    │
│  └─────────────────┬───────────────────────────────────────────┘
│                    │ HTTP/REST
└────────────────────┼─────────────────────────────────────────────
                     │
┌────────────────────▼─────────────────────────────────────────────┐
│                Application Layer                                 │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │              CustomerJourneyService                         │ │
│  │                 (Orchestrator)                              │ │
│  └─────────┬─────────────┬─────────────┬─────────────┬─────────┘ │
│            │Commands     │Commands     │Commands     │Events     │
│            ▼             ▼             ▼             ▲           │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ │
│  │DataProcure- │ │  Offering   │ │  Checkout   │ │Event        │ │
│  │ment         │ │  Commands   │ │  Commands   │ │Listeners    │ │
│  │Commands     │ │             │ │             │ │             │ │
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘ │
└────────────────────┼─────────────┼─────────────┼─────────────────
                     │             │             │
┌────────────────────▼─────────────▼─────────────▼─────────────────┐
│                    Domain Layer                                  │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ │
│  │DataProcure- │ │  Offering   │ │  Checkout   │ │CustomerJour-│ │
│  │ment         │ │  Module     │ │  Module     │ │ney Module   │ │
│  │Module       │ │             │ │             │ │             │ │
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘ │
└────────────────────┼─────────────┼─────────────┼─────────────────
                     │             │             │
┌────────────────────▼─────────────▼─────────────▼─────────────────┐
│                Infrastructure Layer                              │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ │
│  │TIA Data     │ │TIA Pricing  │ │TIA Policy   │ │Database     │ │
│  │Adapters     │ │Adapters     │ │Adapters     │ │Repositories │ │
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

**Command Flow Architecture**
```java
// Request Processing Flow
@RestController
public class CustomerJourneyController {
    
    @PostMapping("/journeys/{id}/data-procurement/start")
    public ResponseEntity<DataProcurementResult> startDataProcurement(
        @PathVariable CustomerJourneyId id,
        @RequestBody DataProcurementRequest request) {
        
        // 1. Web layer receives HTTP request
        var context = new DataProcurementContext(id, request.getCustomerId(), request.getChannel());
        
        // 2. Delegate to application layer orchestrator
        var result = customerJourneyService.startDataProcurement(context);
        
        // 3. Return response to client
        return ResponseEntity.ok(result);
    }
}

@Service
public class CustomerJourneyService {
    
    public DataProcurementResult startDataProcurement(DataProcurementContext context) {
        // 4. Load current journey state
        var journey = journeyRepository.findById(context.getJourneyId());
        
        // 5. Validate state transition
        journey.validateCanStartDataProcurement();
        
        // 6. Execute command on target module
        var result = dataProcurementCommands.processCustomerData(context);
        
        // 7. Update journey state based on result
        journey.updateDataProcurementState(new DataProcurementState(
            result.getId(), result.isComplete(), result.getCompletedAt()));
            
        // 8. Save updated state
        journeyRepository.save(journey);
        
        // 9. Return result to web layer
        return result;
    }
}
```

**Event Flow Architecture**
```java
// Event Publishing and Handling Flow
@Service
public class DataProcurementService implements DataProcurementCommands {
    
    public DataProcurementResult processCustomerData(DataProcurementContext context) {
        // 1. Execute business logic in domain module
        var procurement = new DataProcurement(context);
        var result = procurement.processData();
        
        // 2. Persist domain state
        procurementRepository.save(procurement);
        
        // 3. Publish domain event
        eventPublisher.publishEvent(new DataProcurementCompletedEvent(
            context.getJourneyId(),
            procurement.getId(),
            result.getStatus()
        ));
        
        return result;
    }
}

@Component
public class CustomerJourneyEventHandler {
    
    @EventListener
    @Transactional(propagation = Propagation.REQUIRED)
    public void onDataProcurementCompleted(DataProcurementCompletedEvent event) {
        // 4. Event received by orchestrator
        var journey = journeyRepository.findById(event.getJourneyId());
        
        // 5. Determine next workflow step
        if (journey.canTransitionToOffering()) {
            // 6. Trigger next command
            var offeringResult = offeringCommands.calculateOffering(
                new OfferingContext(event.getProcurementId(), journey.getProductVersion())
            );
            
            // 7. Update journey state
            journey.transitionTo(WorkflowStep.OFFERING);
            journey.updateOfferingState(new OfferingState(
                offeringResult.getId(), OfferingStatus.CALCULATED));
        }
        
        // 8. Persist state changes
        journeyRepository.save(journey);
    }
}
```

### 4.4 Runtime Architecture & Process Flow

**Application Startup and Initialization**
```java
// Spring Boot Application Structure
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.company.insurance.*.infrastructure")
@ComponentScan(basePackages = "com.company.insurance")
public class InsuranceSalesApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(InsuranceSalesApplication.class, args);
    }
}

// Module Configuration
@Configuration
@EnableConfigurationProperties({
    TiaIntegrationProperties.class,
    WorkflowProperties.class,
    SecurityProperties.class
})
public class ApplicationConfiguration {
    
    @Bean
    public ApplicationEventMulticaster applicationEventMulticaster() {
        // Configure synchronous event processing for consistency
        SimpleApplicationEventMulticaster eventMulticaster = 
            new SimpleApplicationEventMulticaster();
        eventMulticaster.setTaskExecutor(new SyncTaskExecutor());
        return eventMulticaster;
    }
    
    @Bean
    public CommandRegistry commandRegistry(
            DataProcurementCommands dataProcurementCommands,
            OfferingCommands offeringCommands,
            CheckoutCommands checkoutCommands) {
        return new CommandRegistry(dataProcurementCommands, offeringCommands, checkoutCommands);
    }
}
```

**Request Processing Flow**
```
1. HTTP Request Reception
   ┌─────────────────────────────────────────────────────────────┐
   │ Spring MVC Controller Layer                                 │
   │ • Request validation and deserialization                   │
   │ • Security context establishment                           │
   │ • Error handling and response formatting                   │
   └─────────────────┬───────────────────────────────────────────┘
                     │
2. Application Service Coordination
   ┌─────────────────▼───────────────────────────────────────────┐
   │ CustomerJourneyService (Orchestrator)                      │
   │ • Journey state loading and validation                     │
   │ • Command routing to appropriate modules                   │
   │ • Transaction boundary management                          │
   │ • Event coordination and state updates                     │
   └─────────────────┬───────────────────────────────────────────┘
                     │
3. Domain Module Processing
   ┌─────────────────▼───────────────────────────────────────────┐
   │ Module Command Handlers                                     │
   │ • Business logic execution                                  │
   │ • Domain validation and rules enforcement                  │
   │ • External system integration                              │
   │ • Domain event generation                                  │
   └─────────────────┬───────────────────────────────────────────┘
                     │
4. Infrastructure Integration
   ┌─────────────────▼───────────────────────────────────────────┐
   │ Adapter Layer                                              │
   │ • TIA system integration                                   │
   │ • Database persistence                                     │
   │ • External service calls                                   │
   │ • Error handling and retry logic                           │
   └─────────────────────────────────────────────────────────────┘
```

**Event Processing Pipeline**
```java
// Event Processing Configuration
@Configuration
public class EventConfiguration {
    
    @Bean
    @Primary
    public ApplicationEventPublisher applicationEventPublisher(
            ApplicationContext applicationContext) {
        return new ApplicationEventPublisher() {
            @Override
            public void publishEvent(Object event) {
                // Add correlation tracking
                if (event instanceof DomainEvent) {
                    ((DomainEvent) event).setCorrelationId(getCurrentCorrelationId());
                    ((DomainEvent) event).setTimestamp(Instant.now());
                }
                applicationContext.publishEvent(event);
            }
        };
    }
}

// Event Processing Flow
┌─────────────────────────────────────────────────────────────────┐
│                    Event Processing Pipeline                    │
├─────────────────────────────────────────────────────────────────┤
│ 1. Event Publication                                            │
│    • Domain service publishes event after state change         │
│    • Event includes correlation ID and metadata                │
│                                                                 │
│ 2. Event Routing                                               │
│    • Spring ApplicationEventMulticaster routes to listeners    │
│    • Synchronous processing within same transaction            │
│                                                                 │
│ 3. Event Handling                                              │
│    • CustomerJourney event handlers process workflow events    │
│    • Cross-module coordination and state updates               │
│                                                                 │
│ 4. Cascade Processing                                          │
│    • Event handlers may trigger additional commands            │
│    • Secondary events published for complex workflows          │
│                                                                 │
│ 5. Audit Trail                                                │
│    • All events logged for regulatory compliance               │
│    • Event sourcing capabilities for debugging                 │
└─────────────────────────────────────────────────────────────────┘
```

**Transaction Management Strategy**
```java
// Transaction Configuration
@Configuration
@EnableTransactionManagement
public class TransactionConfiguration {
    
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
    
    @Bean
    public TransactionTemplate transactionTemplate(
            PlatformTransactionManager transactionManager) {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return template;
    }
}

// Transaction Boundary Strategy
@Service
@Transactional
public class CustomerJourneyService {
    
    // Single transaction for command + immediate state update
    @Transactional
    public DataProcurementResult startDataProcurement(DataProcurementContext context) {
        // All operations within single transaction boundary:
        // 1. Load journey state
        // 2. Execute module command
        // 3. Handle immediate events
        // 4. Update journey state
        // 5. Persist all changes
        // If any step fails, entire transaction rolls back
    }
    
    // Separate transaction for complex workflow progression
    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onComplexWorkflowEvent(WorkflowEvent event) {
        // New transaction allows partial success:
        // Core work is already committed
        // Workflow progression can fail and retry independently
    }
}
```

### 4.5 Deployment Architecture

**Single Application Deployment Model**
```yaml
Deployment Unit: Single Spring Boot JAR
├── Application Runtime
│   ├── Embedded Tomcat Server (Port 8080)
│   ├── JVM Configuration (Java 21, 2GB heap)
│   └── Spring Boot Application Context
├── Database Connectivity
│   ├── Oracle JDBC Connection Pool (HikariCP)
│   ├── Connection Limits: 20 max, 5 min
│   └── Transaction Management (Spring)
├── External Integrations
│   ├── TIA System (Oracle Direct Connect)
│   ├── HTTP Client Pool (Apache HttpClient)
│   └── Circuit Breaker Configuration
└── Monitoring & Observability
    ├── Spring Boot Actuator Endpoints
    ├── Micrometer Metrics Export
    └── Structured Logging (JSON format)
```

**Environment Architecture**
```
┌─────────────────────────────────────────────────────────────────┐
│                      Production Environment                     │
├─────────────────────────────────────────────────────────────────┤
│  Load Balancer (HAProxy/F5)                                    │
│  ├── SSL Termination                                           │
│  ├── Health Check Configuration                                │
│  └── Session Affinity (if needed)                              │
│                              │                                  │
│  ┌───────────────────────────▼────────────────────────────────┐ │
│  │              Application Servers (2-3 instances)           │ │
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌──────────────┐ │ │
│  │  │   App Server 1  │  │   App Server 2  │  │App Server 3  │ │ │
│  │  │                 │  │                 │  │              │ │ │
│  │  │ Spring Boot App │  │ Spring Boot App │  │Spring Boot   │ │ │
│  │  │ Port: 8080      │  │ Port: 8080      │  │App           │ │ │
│  │  │ JVM: 2GB        │  │ JVM: 2GB        │  │Port: 8080    │ │ │
│  │  └─────────────────┘  └─────────────────┘  └──────────────┘ │ │
│  └─────────────────────────┬───────────────────────────────────┘ │
│                            │                                    │
│  ┌─────────────────────────▼───────────────────────────────────┐ │
│  │                 Oracle Database                             │ │
│  │  ┌─────────────────┐  ┌─────────────────┐                  │ │
│  │  │   Primary DB    │  │   Standby DB    │                  │ │
│  │  │                 │  │   (Read Only)   │                  │ │
│  │  │ • Application   │  │ • Disaster      │                  │ │
│  │  │   Schema        │  │   Recovery      │                  │ │
│  │  │ • TIA Schema    │  │ • Reporting     │                  │ │
│  │  └─────────────────┘  └─────────────────┘                  │ │
│  └─────────────────────────────────────────────────────────────┘ │
│                                                                 │
│  External Systems                                               │
│  ┌─────────────────┐              ┌─────────────────┐           │
│  │   TIA System    │              │  Monitoring     │           │
│  │                 │              │                 │           │
│  │ • Policy Mgmt   │              │ • Prometheus    │           │
│  │ • Underwriting  │              │ • Grafana       │           │
│  │ • Earnix Proxy  │              │ • AlertManager  │           │
│  └─────────────────┘              └─────────────────┘           │
└─────────────────────────────────────────────────────────────────┘
```

**Infrastructure Configuration**
```yaml
# application-prod.yml
spring:
  datasource:
    url: jdbc:oracle:thin:@//prod-oracle:1521/INSURANCE
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      
  jpa:
    database-platform: org.hibernate.dialect.Oracle12cDialect
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        jdbc:
          batch_size: 20
        order_inserts: true
        order_updates: true

server:
  port: 8080
  servlet:
    context-path: /insurance-sales
  tomcat:
    max-connections: 200
    max-threads: 50

management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized

logging:
  level:
    com.company.insurance: INFO
    org.springframework.transaction: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

**Scaling and High Availability Strategy**
```yaml
Horizontal Scaling:
  Current: 2-3 application server instances
  Scaling Trigger: CPU > 70% or Memory > 80% for 5 minutes
  Max Instances: 5 (limited by database connection pool)
  
Load Distribution:
  Algorithm: Round-robin with health checks
  Session Management: Stateless (JWT tokens)
  Health Check: /actuator/health endpoint
  
Database Scaling:
  Read Replicas: Available for reporting queries
  Connection Pooling: HikariCP with 20 max connections per instance
  Query Optimization: Indexed queries with < 100ms response time
  
Disaster Recovery:
  RTO (Recovery Time Objective): 4 hours
  RPO (Recovery Point Objective): 1 hour
  Backup Strategy: Daily full backup, hourly incremental
  Failover: Manual failover to standby database
```

**Monitoring and Observability**
```yaml
Application Metrics:
  - JVM metrics (heap, GC, threads)
  - HTTP request metrics (latency, error rate)
  - Database connection pool metrics
  - Custom business metrics (journey completion rates)
  
System Metrics:
  - CPU, memory, disk utilization
  - Network latency and throughput
  - Database performance metrics
  
Alerting Rules:
  - Application error rate > 5%
  - Response time > 2 seconds (95th percentile)
  - Database connection pool exhaustion
  - TIA integration failures > 10%
  
Log Aggregation:
  - Centralized logging with ELK stack
  - Structured JSON logs with correlation IDs
  - Log retention: 90 days for audit compliance
```

## 5. Domain Module Design

### 5.1 CustomerJourney Module (Orchestrator)

#### 5.1.1 Responsibilities & Boundaries

**Primary Responsibilities:**
- **Workflow State Management**: Authoritative state of sales process across all modules
- **Command Coordination**: Route commands to appropriate modules and manage execution
- **Event Processing**: Handle domain events and coordinate workflow progression
- **Cross-Module Integration**: Single integration point between domain modules
- **Business Process Enforcement**: Ensure workflow follows business rules and state transitions
- **Audit Trail Management**: Comprehensive audit logs for regulatory compliance

**Key Architectural Decisions:**
```java
// Minimal state exposure - modules only expose essential state
@Embedded
private DataProcurementState dataProcurementState; // Just ID + completion status
@Embedded  
private OfferingState offeringState;               // Just ID + status + acceptance
@Embedded
private CheckoutState checkoutState;               // Just ID + payment status

// Rich domain logic stays within respective modules
// CustomerJourney doesn't know HOW pricing works, just WHEN it's complete
```

**Boundary Enforcement:**
- CustomerJourney → All modules (orchestrator privilege)
- Modules → CustomerJourney (events only, no direct dependencies)
- Modules ↔ Modules (forbidden - must go through CustomerJourney)

#### 5.1.2 Workflow State Management

**State Machine Pattern Implementation:**
```java
public enum WorkflowStep {
    DATA_PROCUREMENT {
        @Override
        public Set<WorkflowStep> getAllowedTransitions() {
            return Set.of(OFFERING, DATA_PROCUREMENT);
        }
        
        @Override
        public void onDataChange(CustomerJourney journey) {
            // Data changes in this step don't cascade
        }
    },
    OFFERING {
        @Override
        public void onDataChange(CustomerJourney journey) {
            journey.resetOfferingState();    // Reset self
            journey.resetCheckoutState();    // Cascade reset
        }
    }
    // ... other states
}
```

**Concurrency Control:**
```java
// 2-hour rule implementation
public boolean canBeModifiedBy(UserId userId, ChannelType channel) {
    if (lastModifiedBy != null && lastModifiedBy.equals(userId)) {
        return true; // Same user exception
    }
    return lastModified.isBefore(LocalDateTime.now().minusHours(2));
}
```

#### 5.1.3 Command Coordination Patterns

**Command Interface Pattern:**
```java
// Generic command execution with state management
public <T> T executeCommand(Command<T> command) {
    var journey = loadJourneyWithLocking(command.getJourneyId());
    validateCommandExecution(command, journey);
    ensureUserCanModifyJourney(command.getExecutingUser(), journey);
    
    T result = commandRegistry.executeCommand(command);
    updateJourneyStateFromCommandResult(command, result, journey);
    
    journey.markAsModifiedBy(command.getExecutingUser(), getCurrentChannel());
    journeyRepository.save(journey);
    
    return result;
}
```

#### 5.1.4 Event Handling Strategy

**Event-Driven Workflow Progression:**
```java
@EventListener
@Transactional(propagation = Propagation.REQUIRED)
public void onDataProcurementCompleted(DataProcurementCompletedEvent event) {
    var journey = journeyRepository.findById(event.getJourneyId());
    journey.updateDataProcurementState(new DataProcurementState(
        event.getDataProcurementId(), true, event.getCompletedAt()));
    
    // Auto-progression logic
    if (workflowProgressionService.shouldAutoProgressToOffering(journey)) {
        triggerOfferingCalculation(journey, event.getDataProcurementId());
    }
}
```

### 5.2 DataProcurement Module

#### 5.2.1 Business Capabilities & Domain Logic

**Core Domain Responsibilities:**
- **Data Collection**: Gather customer, vehicle, and insurance history
- **Data Validation & Enrichment**: Validate and enrich with external sources
- **Data Transformation**: Transform user input to TIA-compatible formats
- **Quality Assurance**: Ensure completeness for downstream processing

**Domain Model Design:**
```java
@Entity
public class DataProcurement {
    // Core domain logic - not exposed to other modules
    public DataProcurementResult processCustomerData(CustomerDataInput input) {
        validateInput(input);
        transformAndEnrichData(input);
        var validationResults = validateEnrichedData();
        updateCompletionStatus();
        return buildResult();
    }
    
    // State management for workflow coordination
    public boolean isComplete() {
        return completionPercentage.equals(BigDecimal.valueOf(100)) 
            && hasNoBlockingValidationErrors();
    }
}
```

#### 5.2.2 Command Interface Design

**Clean Command Boundaries:**
```java
public interface DataProcurementCommands {
    DataProcurementResult processCustomerData(DataProcurementContext context);
    DataProcurementResult updateCustomerData(DataProcurementId id, CustomerDataUpdate update);
    ValidationResult validateCurrentData(DataProcurementId id);
    void cancelDataProcurement(DataProcurementId id, String reason);
}

// Context objects hide internal complexity
public record DataProcurementContext(
    CustomerJourneyId journeyId,    // For correlation
    CustomerId customerId,          // Business identifier
    ChannelType channel,            // For channel-specific rules
    UserId initiatingUser,          // For audit
    CustomerDataInput initialData   // Optional initial data
) {}
```

#### 5.2.3 Data Transformation Patterns

**TIA Integration Abstraction:**
```java
@Component
public class TiaDataTransformationService {
    
    // Transform internal model to TIA format
    public TiaCustomerDataRequest transformForPricing(CustomerData customerData, VehicleData vehicleData) {
        return TiaCustomerDataRequest.builder()
            .customerInfo(transformCustomerInfo(customerData))
            .vehicleInfo(transformVehicleInfo(vehicleData))
            .riskFactors(calculateRiskFactors(customerData, vehicleData))
            .build();
    }
    
    // Handle TIA response complexity internally
    public EnrichedCustomerData transformFromTiaResponse(TiaCustomerDataResponse tiaResponse) {
        // Complex mapping logic hidden from domain
    }
}
```

#### 5.2.4 Validation & Error Handling

**Validation Framework:**
```java
public class ValidationResult {
    public static ValidationResult error(String code, String message) { /* ... */ }
    public static ValidationResult warning(String code, String message) { /* ... */ }
    public static ValidationResult manualReview(String code, String message) { /* ... */ }
    
    public boolean blocksProcessing() { return severity == ValidationSeverity.ERROR; }
    public boolean requiresManualReview() { return requiresManualReview; }
}

// Domain-specific exceptions with context
public class InvalidCustomerDataException extends DataProcurementException {
    private final List<ValidationResult> validationErrors;
    // Provides actionable error information to UI layer
}
```

### 5.3 Offering Module

#### 5.3.1 Pricing Logic & Business Rules

**Pricing Calculation Architecture:**
```java
@Entity
public class Offering {
    
    // Multi-stage pricing calculation
    public OfferingResult calculatePricing(CustomerData customerData, VehicleData vehicleData) {
        // 1. Get base rates from TIA/Earnix
        var baseRates = pricingEngine.calculateBaseRates(/* ... */);
        
        // 2. Apply business rule adjustments
        var adjustedRates = applyBusinessRuleAdjustments(baseRates, customerData, vehicleData);
        
        // 3. Calculate coverage options
        this.coverageOptions = calculateCoverageOptions(adjustedRates, customerData, vehicleData);
        
        // 4. Apply discounts
        this.applicableDiscounts = calculateApplicableDiscounts(customerData, vehicleData);
        
        // 5. Calculate final pricing
        this.finalPricing = applyDiscounts(basePricing, applicableDiscounts);
        
        return buildOfferingResult();
    }
    
    // Business rules encapsulated in domain model
    private List<Discount> calculateApplicableDiscounts(CustomerData customerData, VehicleData vehicleData) {
        var discounts = new ArrayList<Discount>();
        
        // Multi-policy discount
        if (hasExistingPolicies(customerData.getCustomerId())) {
            discounts.add(createMultiPolicyDiscount());
        }
        
        // Safe driver discount
        if (customerData.getDrivingHistory().isCleanRecord(Duration.ofYears(3))) {
            discounts.add(createSafeDriverDiscount());
        }
        
        return discounts;
    }
}
```

#### 5.3.2 TIA/Earnix Integration Patterns

**Integration Adapter Design:**
```java
@Component
public class TiaPricingAdapter {
    
    // Circuit breaker pattern for resilience
    public BaseRates calculateBaseRates(PricingInput input) {
        return circuitBreaker.executeSupplier(() -> 
            retryTemplate.execute(context -> doCalculateBaseRates(input))
        );
    }
    
    // TIA stored procedure integration
    private BaseRates doCalculateBaseRates(PricingInput input) {
        var sql = "{call PKG_PRICING.CALCULATE_RATES(?, ?, ?, ?, ?, ?, ?)}";
        // Execute TIA procedure and handle response
        return mapper.fromTiaPricingResponse(tiaResponse);
    }
    
    // Earnix integration through TIA proxy
    public EarnixRateFactors getEarnixRateFactors(EarnixInput input) {
        var sql = "{call PKG_EARNIX.GET_RATE_FACTORS(?, ?, ?, ?)}";
        // Earnix accessible only through TIA procedures
    }
}
```

#### 5.3.3 Product Versioning Strategy

**Version Locking for Price Guarantees:**
```java
@Entity
public class Offering {
    @Embedded
    private ProductVersion productVersion; // Lock version at calculation time
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;       // 30-day expiration policy
    
    // Version compatibility checking
    public boolean needsRecalculation(ProductVersion currentVersion) {
        return !this.productVersion.equals(currentVersion) || isExpired();
    }
    
    // Handle version changes during long-running processes
    public OfferingResult recalculate(RecalculationReason reason) {
        this.productVersion = ProductVersion.current();
        return calculatePricing(/* updated data */);
    }
}
```

#### 5.3.4 Performance Optimization

**Caching Strategy:**
```java
@Service
public class OfferingCacheService {
    
    // Cache base rates for similar customer profiles
    @Cacheable(value = "baseRates", key = "#input.cacheKey()")
    public BaseRates getBaseRates(PricingInput input) {
        return tiaPricingAdapter.calculateBaseRates(input);
    }
    
    // Cache discount calculations
    @Cacheable(value = "discounts", key = "#customerProfile.hashCode()")
    public List<Discount> getApplicableDiscounts(CustomerProfile customerProfile) {
        return discountEngine.calculateDiscounts(customerProfile);
    }
}
```

### 5.4 Checkout Module

#### 5.4.1 Payment Processing Architecture

**Payment Workflow Design:**
```java
@Entity
public class Checkout {
    
    // Multi-step checkout process
    public CheckoutResult processCheckout(CheckoutContext context) {
        validateOfferingAcceptance(context.getOfferingId());
        
        // 1. Initialize checkout with accepted offering
        initializeFromOffering(context.getOfferingId());
        
        // 2. Process payment
        var paymentResult = processPayment(context.getPaymentDetails());
        
        // 3. Create policy if payment successful
        if (paymentResult.isSuccessful()) {
            var policyResult = createPolicy();
            return CheckoutResult.successful(paymentResult, policyResult);
        }
        
        return CheckoutResult.failed(paymentResult.getFailureReason());
    }
}
```

#### 5.4.2 Policy Creation Workflow

**TIA Policy Integration:**
```java
@Component
public class TiaPolicyAdapter {
    
    // Policy creation through TIA
    public PolicyCreationResult createPolicy(AcceptedOffering offering) {
        var tiaRequest = buildTiaPolicyRequest(offering);
        
        try {
            var sql = "{call PKG_POLICY.CREATE_POLICY(?, ?, ?, ?)}";
            var policyNumber = executePolicyCreation(sql, tiaRequest);
            return PolicyCreationResult.successful(policyNumber);
        } catch (TiaIntegrationException e) {
            return PolicyCreationResult.failed(e.getMessage());
        }
    }
}
```

#### 5.4.3 Idempotency & Safety Mechanisms

**Payment Safety Guards:**
```java
@Service
public class PaymentService {
    
    // Idempotency for payment operations
    public PaymentResult processPayment(CheckoutId checkoutId, PaymentDetails payment) {
        // Check for existing successful payment
        var existingPayment = paymentRepository.findSuccessfulPaymentByCheckoutId(checkoutId);
        if (existingPayment.isPresent()) {
            return PaymentResult.alreadyProcessed(existingPayment.get());
        }
        
        // Process new payment with idempotency key
        var idempotencyKey = generateIdempotencyKey(checkoutId, payment);
        return paymentGateway.processPayment(payment, idempotencyKey);
    }
}
```

#### 5.4.4 Compliance & Security Implementation

**Regulatory Compliance:**
```java
@Component
public class ComplianceValidator {
    
    // State-specific compliance checks
    public ComplianceResult validateCompliance(PolicyRequest request) {
        var state = request.getCustomerAddress().getState();
        var requirements = regulatoryService.getRequirements(state);
        
        var results = new ArrayList<ComplianceResult>();
        results.add(validateMandatoryCoverage(request, requirements));
        results.add(validatePolicyLimits(request, requirements));
        results.add(validateDocumentationRequirements(request, requirements));
        
        return ComplianceResult.aggregate(results);
    }
}
```

### 5.5 Shared Components

#### 5.5.1 Common Infrastructure

**TIA Integration Layer:**
```java
@Configuration
public class TiaIntegrationConfiguration {
    
    @Bean
    @Primary
    public DataSource tiaDataSource() {
        // Oracle connection pool configuration
        var config = HikariConfig();
        config.setJdbcUrl(tiaProperties.getJdbcUrl());
        config.setMaximumPoolSize(20);
        config.setConnectionTimeout(30000);
        return new HikariDataSource(config);
    }
    
    @Bean
    public CircuitBreaker tiaCircuitBreaker() {
        return CircuitBreaker.ofDefaults("tia-integration");
    }
}
```

#### 5.5.2 Event Infrastructure

**Domain Event Framework:**
```java
// Base domain event
public abstract class DomainEvent {
    private final CustomerJourneyId journeyId;
    private final LocalDateTime timestamp;
    private final String correlationId;
    
    // Event publishing pattern
    protected void publishEvent(ApplicationEventPublisher publisher) {
        this.timestamp = LocalDateTime.now();
        this.correlationId = getCurrentCorrelationId();
        publisher.publishEvent(this);
    }
}

// Specific events
public class DataProcurementCompletedEvent extends DomainEvent {
    private final DataProcurementId dataProcurementId;
    private final LocalDateTime completedAt;
}
```

#### 5.5.3 Security & Audit

**Audit Trail Implementation:**
```java
@Component
public class AuditService {
    
    @EventListener
    public void auditDomainEvent(DomainEvent event) {
        var auditRecord = AuditRecord.builder()
            .eventType(event.getClass().getSimpleName())
            .journeyId(event.getJourneyId())
            .timestamp(event.getTimestamp())
            .correlationId(event.getCorrelationId())
            .eventData(JsonUtils.toJson(event))
            .build();
            
        auditRepository.save(auditRecord);
    }
}
```

---

*This module design provides clear boundaries, responsibilities, and interaction patterns while maintaining the architectural principles established in previous sections. Each module encapsulates its domain logic while providing clean interfaces for coordination through the CustomerJourney orchestrator.*

## 6. Command-Driven Architecture Implementation

### 6.1 Command Pattern Design & Interfaces

#### 6.1.1 Command Interface Hierarchy

**Base Command Abstraction:**
```java
public interface Command<T> {
    // Identity and routing
    CommandType getCommandType();
    ModuleType getTargetModule();
    CustomerJourneyId getJourneyId();
    
    // Security and audit context
    UserId getExecutingUser();
    ChannelType getChannel();
    String getCorrelationId();
    
    // Validation contract
    ValidationResult validate();
    
    // Idempotency support
    default String getIdempotencyKey() {
        return getCommandType() + ":" + getJourneyId() + ":" + hashCode();
    }
}

// Module-specific command interfaces
public interface DataProcurementCommand<T> extends Command<T> {
    @Override
    default ModuleType getTargetModule() { return ModuleType.DATA_PROCUREMENT; }
}

public interface OfferingCommand<T> extends Command<T> {
    @Override  
    default ModuleType getTargetModule() { return ModuleType.OFFERING; }
}
```

**Concrete Command Implementations:**
```java
// Data procurement commands
public record StartDataProcurementCommand(
    CustomerJourneyId journeyId,
    CustomerId customerId,
    ChannelType channel,
    UserId executingUser,
    CustomerDataInput initialData
) implements DataProcurementCommand<DataProcurementResult> {
    
    @Override
    public CommandType getCommandType() { return CommandType.START_DATA_PROCUREMENT; }
    
    @Override
    public ValidationResult validate() {
        if (journeyId == null) return ValidationResult.error("JOURNEY_ID_REQUIRED");
        if (customerId == null) return ValidationResult.error("CUSTOMER_ID_REQUIRED");
        if (executingUser == null) return ValidationResult.error("USER_REQUIRED");
        return ValidationResult.success();
    }
}

public record UpdateCustomerDataCommand(
    CustomerJourneyId journeyId,
    DataProcurementId dataProcurementId,
    CustomerDataUpdate dataUpdate,
    UserId executingUser,
    ChannelType channel
) implements DataProcurementCommand<DataProcurementResult> {
    
    @Override
    public CommandType getCommandType() { return CommandType.UPDATE_CUSTOMER_DATA; }
    
    @Override
    public ValidationResult validate() {
        var result = ValidationResult.success();
        if (dataProcurementId == null) return ValidationResult.error("PROCUREMENT_ID_REQUIRED");
        if (dataUpdate == null || dataUpdate.isEmpty()) return ValidationResult.error("UPDATE_DATA_REQUIRED");
        return result;
    }
}

// Offering commands
public record CalculateOfferingCommand(
    CustomerJourneyId journeyId,
    DataProcurementId dataProcurementId,
    ProductVersion productVersion,
    ChannelType channel,
    UserId executingUser
) implements OfferingCommand<OfferingResult> {
    
    @Override
    public CommandType getCommandType() { return CommandType.CALCULATE_OFFERING; }
    
    @Override
    public ValidationResult validate() {
        if (dataProcurementId == null) return ValidationResult.error("PROCUREMENT_ID_REQUIRED");
        if (productVersion == null) return ValidationResult.error("PRODUCT_VERSION_REQUIRED");
        return ValidationResult.success();
    }
}
```

#### 6.1.2 Command Handler Pattern

**Module Command Handler Interfaces:**
```java
public interface CommandHandler<C extends Command<T>, T> {
    /**
     * Handle the command and return result
     */
    T handle(C command);
    
    /**
     * Check if this handler can process the command
     */
    boolean canHandle(Command<?> command);
    
    /**
     * Pre-execution validation specific to this handler
     */
    default ValidationResult validateExecution(C command) {
        return ValidationResult.success();
    }
}

// Module-specific handler interfaces
public interface DataProcurementCommandHandler<C extends DataProcurementCommand<T>, T> 
    extends CommandHandler<C, T> {
    
    @Override
    default boolean canHandle(Command<?> command) {
        return command instanceof DataProcurementCommand;
    }
}
```

**Concrete Command Handlers:**
```java
@Component
public class StartDataProcurementHandler 
    implements DataProcurementCommandHandler<StartDataProcurementCommand, DataProcurementResult> {
    
    private final DataProcurementService dataProcurementService;
    private final ApplicationEventPublisher eventPublisher;
    
    @Override
    public DataProcurementResult handle(StartDataProcurementCommand command) {
        log.info("Handling start data procurement for journey: {}", command.journeyId());
        
        // Create context from command
        var context = new DataProcurementContext(
            command.journeyId(),
            command.customerId(),
            command.channel(),
            command.executingUser(),
            command.initialData()
        );
        
        // Execute business logic
        var result = dataProcurementService.processCustomerData(context);
        
        // Publish success event
        eventPublisher.publishEvent(new CommandExecutedEvent(
            command.getCommandType(),
            command.getJourneyId(),
            command.getExecutingUser(),
            true,
            null
        ));
        
        return result;
    }
    
    @Override
    public ValidationResult validateExecution(StartDataProcurementCommand command) {
        // Business-specific validation
        if (dataProcurementService.hasActiveDataProcurement(command.journeyId())) {
            return ValidationResult.error("DATA_PROCUREMENT_ALREADY_ACTIVE");
        }
        return ValidationResult.success();
    }
    
    @Override
    public boolean canHandle(Command<?> command) {
        return command instanceof StartDataProcurementCommand;
    }
}
```

#### 6.1.3 Command Registry Pattern

**Dynamic Command Handler Registration:**
```java
@Component
public class CommandRegistry {
    
    private final Map<CommandType, CommandHandler<?, ?>> handlers = new ConcurrentHashMap<>();
    
    public CommandRegistry(List<CommandHandler<?, ?>> commandHandlers) {
        // Register all handlers by scanning command types
        registerHandlers(commandHandlers);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T executeCommand(Command<T> command) {
        var handler = (CommandHandler<Command<T>, T>) handlers.get(command.getCommandType());
        
        if (handler == null) {
            throw new UnsupportedCommandException(command.getCommandType());
        }
        
        if (!handler.canHandle(command)) {
            throw new InvalidCommandHandlerException(command.getCommandType(), handler.getClass());
        }
        
        return handler.handle(command);
    }
    
    private void registerHandlers(List<CommandHandler<?, ?>> commandHandlers) {
        for (var handler : commandHandlers) {
            var commandType = extractCommandType(handler);
            handlers.put(commandType, handler);
            log.debug("Registered command handler: {} for command type: {}", 
                handler.getClass().getSimpleName(), commandType);
        }
    }
    
    private CommandType extractCommandType(CommandHandler<?, ?> handler) {
        // Use reflection or annotation to determine command type
        var handlerClass = handler.getClass();
        var handlesAnnotation = handlerClass.getAnnotation(Handles.class);
        if (handlesAnnotation != null) {
            return handlesAnnotation.value();
        }
        
        // Fallback: extract from generic type parameters
        return extractFromGenericTypes(handlerClass);
    }
}

// Handler registration annotation
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Handles {
    CommandType value();
}
```

### 6.2 Command Processing Pipeline

#### 6.2.1 Command Execution Flow

**Pipeline Architecture:**
```java
@Service
@Transactional
public class CommandProcessor {
    
    private final List<CommandInterceptor> interceptors;
    private final CommandRegistry commandRegistry;
    private final CommandAuditService auditService;
    
    public <T> T processCommand(Command<T> command) {
        var executionContext = new CommandExecutionContext(command);
        
        try {
            // 1. Pre-processing pipeline
            executePreProcessingInterceptors(command, executionContext);
            
            // 2. Command validation
            validateCommand(command, executionContext);
            
            // 3. Execute command
            T result = commandRegistry.executeCommand(command);
            executionContext.setResult(result);
            
            // 4. Post-processing pipeline  
            executePostProcessingInterceptors(command, executionContext);
            
            // 5. Audit successful execution
            auditService.auditSuccessfulExecution(command, result, executionContext);
            
            return result;
            
        } catch (Exception e) {
            // Error handling and audit
            handleCommandExecutionError(command, executionContext, e);
            throw e;
        }
    }
    
    private void executePreProcessingInterceptors(Command<?> command, CommandExecutionContext context) {
        for (var interceptor : interceptors) {
            if (interceptor.supportsPreProcessing() && interceptor.appliesTo(command)) {
                interceptor.preProcess(command, context);
            }
        }
    }
    
    private void validateCommand(Command<?> command, CommandExecutionContext context) {
        // 1. Basic command validation
        var basicValidation = command.validate();
        if (!basicValidation.isSuccess()) {
            throw new InvalidCommandException(command, basicValidation);
        }
        
        // 2. Security validation
        var securityValidation = securityValidator.validateCommandExecution(command);
        if (!securityValidation.isSuccess()) {
            throw new CommandSecurityException(command, securityValidation);
        }
        
        // 3. Business rule validation
        var businessValidation = businessRuleValidator.validateCommand(command);
        if (!businessValidation.isSuccess()) {
            throw new BusinessRuleViolationException(command, businessValidation);
        }
        
        context.setValidationResults(basicValidation, securityValidation, businessValidation);
    }
}
```

#### 6.2.2 Command Interceptor Pattern

**Interceptor Framework:**
```java
public interface CommandInterceptor {
    /**
     * Execute before command processing
     */
    void preProcess(Command<?> command, CommandExecutionContext context);
    
    /**
     * Execute after successful command processing
     */
    void postProcess(Command<?> command, CommandExecutionContext context);
    
    /**
     * Execute when command processing fails
     */
    void onError(Command<?> command, CommandExecutionContext context, Exception error);
    
    /**
     * Determine if this interceptor applies to the command
     */
    boolean appliesTo(Command<?> command);
    
    /**
     * Interceptor execution order (lower values execute first)
     */
    int getOrder();
    
    default boolean supportsPreProcessing() { return true; }
    default boolean supportsPostProcessing() { return true; }
    default boolean supportsErrorHandling() { return true; }
}

// Specific interceptors
@Component
@Order(1)
public class SecurityCommandInterceptor implements CommandInterceptor {
    
    private final SecurityContext securityContext;
    
    @Override
    public void preProcess(Command<?> command, CommandExecutionContext context) {
        // Verify user has permission to execute this command
        var permission = determineRequiredPermission(command);
        if (!securityContext.hasPermission(command.getExecutingUser(), permission)) {
            throw new InsufficientPermissionsException(command.getExecutingUser(), permission);
        }
        
        // Log security context
        context.addMetadata("security.user", command.getExecutingUser());
        context.addMetadata("security.channel", command.getChannel());
        context.addMetadata("security.permissions", securityContext.getUserPermissions(command.getExecutingUser()));
    }
    
    @Override
    public boolean appliesTo(Command<?> command) {
        return true; // Security applies to all commands
    }
}

@Component  
@Order(2)
public class ConcurrencyControlInterceptor implements CommandInterceptor {
    
    private final ConcurrencyControlService concurrencyService;
    
    @Override
    public void preProcess(Command<?> command, CommandExecutionContext context) {
        // Check journey modification locks
        if (requiresJourneyLock(command)) {
            var lockResult = concurrencyService.acquireJourneyLock(
                command.getJourneyId(), 
                command.getExecutingUser(),
                command.getChannel()
            );
            
            if (!lockResult.isSuccessful()) {
                throw new ConcurrencyViolationException(
                    command.getJourneyId(), 
                    lockResult.getCurrentLockHolder(),
                    lockResult.getLockExpiresAt()
                );
            }
            
            context.addMetadata("concurrency.lockAcquired", true);
            context.addMetadata("concurrency.lockId", lockResult.getLockId());
        }
    }
    
    @Override
    public boolean appliesTo(Command<?> command) {
        return command instanceof CustomerJourneyModifyingCommand;
    }
}
```

#### 6.2.3 Command Execution Context

**Context Management:**
```java
public class CommandExecutionContext {
    private final Command<?> command;
    private final LocalDateTime startTime;
    private final String executionId;
    private final Map<String, Object> metadata;
    
    private Object result;
    private Duration executionDuration;
    private List<ValidationResult> validationResults;
    private Exception error;
    
    public CommandExecutionContext(Command<?> command) {
        this.command = command;
        this.startTime = LocalDateTime.now();
        this.executionId = UUID.randomUUID().toString();
        this.metadata = new ConcurrentHashMap<>();
        
        // Initialize with command context
        addMetadata("command.type", command.getCommandType());
        addMetadata("command.journeyId", command.getJourneyId());
        addMetadata("command.correlationId", command.getCorrelationId());
    }
    
    public void markCompleted(Object result) {
        this.result = result;
        this.executionDuration = Duration.between(startTime, LocalDateTime.now());
        addMetadata("execution.duration", executionDuration.toMillis());
        addMetadata("execution.successful", true);
    }
    
    public void markFailed(Exception error) {
        this.error = error;
        this.executionDuration = Duration.between(startTime, LocalDateTime.now());
        addMetadata("execution.duration", executionDuration.toMillis());
        addMetadata("execution.successful", false);
        addMetadata("execution.error", error.getClass().getSimpleName());
        addMetadata("execution.errorMessage", error.getMessage());
    }
    
    // Context enrichment methods
    public void addMetadata(String key, Object value) {
        metadata.put(key, value);
    }
    
    public Map<String, Object> getExecutionSummary() {
        var summary = new HashMap<>(metadata);
        summary.put("executionId", executionId);
        summary.put("startTime", startTime);
        summary.put("hasResult", result != null);
        summary.put("hasError", error != null);
        return Map.copyOf(summary);
    }
}
```

### 6.3 Input Validation & Context Propagation

#### 6.3.1 Multi-Layer Validation Strategy

**Validation Pipeline:**
```java
@Component
public class CommandValidationService {
    
    private final List<CommandValidator> validators;
    
    public ValidationResult validateCommand(Command<?> command) {
        var results = new ArrayList<ValidationResult>();
        
        // 1. Structural validation
        results.add(validateCommandStructure(command));
        
        // 2. Business rule validation
        results.add(validateBusinessRules(command));
        
        // 3. State-dependent validation
        results.add(validateCurrentState(command));
        
        // 4. Security validation
        results.add(validateSecurity(command));
        
        return ValidationResult.aggregate(results);
    }
    
    private ValidationResult validateCommandStructure(Command<?> command) {
        // Basic structural validation using Bean Validation
        var constraintViolations = validator.validate(command);
        if (!constraintViolations.isEmpty()) {
            var errors = constraintViolations.stream()
                .map(violation -> ValidationResult.error(
                    "CONSTRAINT_VIOLATION",
                    violation.getPropertyPath() + ": " + violation.getMessage()
                ))
                .collect(Collectors.toList());
            return ValidationResult.aggregate(errors);
        }
        return ValidationResult.success();
    }
    
    private ValidationResult validateBusinessRules(Command<?> command) {
        for (var validator : validators) {
            if (validator.appliesTo(command)) {
                var result = validator.validate(command);
                if (!result.isSuccess()) {
                    return result;
                }
            }
        }
        return ValidationResult.success();
    }
    
    private ValidationResult validateCurrentState(Command<?> command) {
        // Load current journey state and validate command is appropriate
        var journey = journeyService.getJourney(command.getJourneyId());
        var currentStep = journey.getCurrentStep();
        var requiredStep = command.getCommandType().getRequiredWorkflowStep();
        
        if (!currentStep.canTransitionTo(requiredStep)) {
            return ValidationResult.error(
                "INVALID_WORKFLOW_STATE",
                String.format("Cannot execute %s in current state %s", 
                    command.getCommandType(), currentStep)
            );
        }
        
        return ValidationResult.success();
    }
}

// Command-specific validators
public interface CommandValidator {
    ValidationResult validate(Command<?> command);
    boolean appliesTo(Command<?> command);
}

@Component
public class DataProcurementCommandValidator implements CommandValidator {
    
    @Override
    public ValidationResult validate(Command<?> command) {
        if (command instanceof UpdateCustomerDataCommand updateCommand) {
            return validateUpdateCustomerDataCommand(updateCommand);
        }
        return ValidationResult.success();
    }
    
    private ValidationResult validateUpdateCustomerDataCommand(UpdateCustomerDataCommand command) {
        var dataUpdate = command.dataUpdate();
        
        // Validate at least one field is being updated
        if (dataUpdate.isEmpty()) {
            return ValidationResult.error("NO_UPDATES_SPECIFIED", "At least one field must be updated");
        }
        
        // Validate data format and business rules
        if (dataUpdate.hasDateOfBirth() && dataUpdate.getDateOfBirth().isAfter(LocalDate.now())) {
            return ValidationResult.error("INVALID_BIRTH_DATE", "Date of birth cannot be in the future");
        }
        
        return ValidationResult.success();
    }
    
    @Override
    public boolean appliesTo(Command<?> command) {
        return command instanceof DataProcurementCommand;
    }
}
```

#### 6.3.2 Context Propagation Patterns

**Cross-Cutting Context Management:**
```java
@Component
public class ContextPropagationService {
    
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String USER_CONTEXT_HEADER = "X-User-Context";
    private static final String CHANNEL_CONTEXT_HEADER = "X-Channel-Context";
    
    public void propagateContext(Command<?> command) {
        // Set up correlation context for entire request processing
        var correlationId = command.getCorrelationId();
        if (correlationId == null) {
            correlationId = generateCorrelationId();
        }
        
        // Store in thread-local context
        ContextHolder.setCorrelationId(correlationId);
        ContextHolder.setUserContext(command.getExecutingUser());
        ContextHolder.setChannelContext(command.getChannel());
        ContextHolder.setJourneyContext(command.getJourneyId());
        
        // Set up logging context
        MDC.put("correlationId", correlationId);
        MDC.put("userId", command.getExecutingUser().toString());
        MDC.put("channel", command.getChannel().toString());
        MDC.put("journeyId", command.getJourneyId().toString());
        MDC.put("commandType", command.getCommandType().toString());
    }
    
    public void clearContext() {
        ContextHolder.clear();
        MDC.clear();
    }
    
    private String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }
}

// Thread-safe context holder
public class ContextHolder {
    private static final ThreadLocal<RequestContext> contextHolder = new ThreadLocal<>();
    
    public static void setCorrelationId(String correlationId) {
        getOrCreateContext().setCorrelationId(correlationId);
    }
    
    public static String getCorrelationId() {
        var context = contextHolder.get();
        return context != null ? context.getCorrelationId() : null;
    }
    
    public static void setUserContext(UserId userId) {
        getOrCreateContext().setUserId(userId);
    }
    
    public static UserId getUserContext() {
        var context = contextHolder.get();
        return context != null ? context.getUserId() : null;
    }
    
    private static RequestContext getOrCreateContext() {
        var context = contextHolder.get();
        if (context == null) {
            context = new RequestContext();
            contextHolder.set(context);
        }
        return context;
    }
    
    public static void clear() {
        contextHolder.remove();
    }
}
```

### 6.4 Error Handling & Recovery Strategies

#### 6.4.1 Exception Classification Framework

**Hierarchical Exception Design:**
```java
// Base command exception
public abstract class CommandException extends RuntimeException {
    private final Command<?> command;
    private final String errorCode;
    private final Map<String, Object> errorContext;
    
    protected CommandException(String message, Command<?> command, String errorCode) {
        super(message);
        this.command = command;
        this.errorCode = errorCode;
        this.errorContext = new HashMap<>();
        enrichErrorContext();
    }
    
    private void enrichErrorContext() {
        errorContext.put("commandType", command.getCommandType());
        errorContext.put("journeyId", command.getJourneyId());
        errorContext.put("executingUser", command.getExecutingUser());
        errorContext.put("timestamp", LocalDateTime.now());
    }
    
    public abstract ErrorRecoveryStrategy getRecoveryStrategy();
    public abstract ErrorSeverity getSeverity();
}

// Specific exception types
public class BusinessRuleViolationException extends CommandException {
    private final List<ValidationResult> violations;
    
    public BusinessRuleViolationException(Command<?> command, List<ValidationResult> violations) {
        super("Business rule validation failed", command, "BUSINESS_RULE_VIOLATION");
        this.violations = violations;
    }
    
    @Override
    public ErrorRecoveryStrategy getRecoveryStrategy() {
        return ErrorRecoveryStrategy.USER_CORRECTION_REQUIRED;
    }
    
    @Override
    public ErrorSeverity getSeverity() {
        return ErrorSeverity.USER_ERROR;
    }
}

public class ExternalSystemException extends CommandException {
    private final String systemName;
    private final String systemErrorCode;
    
    public ExternalSystemException(String message, Command<?> command, String systemName, Throwable cause) {
        super(message, command, "EXTERNAL_SYSTEM_ERROR");
        this.systemName = systemName;
        this.systemErrorCode = extractErrorCode(cause);
        initCause(cause);
    }
    
    @Override
    public ErrorRecoveryStrategy getRecoveryStrategy() {
        return isTransientError() ? ErrorRecoveryStrategy.RETRY : ErrorRecoveryStrategy.MANUAL_INTERVENTION;
    }
    
    @Override
    public ErrorSeverity getSeverity() {
        return ErrorSeverity.SYSTEM_ERROR;
    }
    
    private boolean isTransientError() {
        return systemErrorCode != null && TRANSIENT_ERROR_CODES.contains(systemErrorCode);
    }
}
```

#### 6.4.2 Error Recovery Mechanisms

**Recovery Strategy Implementation:**
```java
@Component
public class CommandErrorRecoveryService {
    
    private final CommandRetryService retryService;
    private final DeadLetterService deadLetterService;
    private final NotificationService notificationService;
    
    public void handleCommandError(Command<?> command, CommandExecutionContext context, Exception error) {
        var errorClassification = classifyError(error);
        var recoveryStrategy = errorClassification.getRecoveryStrategy();
        
        switch (recoveryStrategy) {
            case RETRY -> handleRetryableError(command, context, error);
            case USER_CORRECTION_REQUIRED -> handleUserCorrectionRequired(command, context, error);
            case MANUAL_INTERVENTION -> handleManualIntervention(command, context, error);
            case FAIL_FAST -> handleFailFast(command, context, error);
        }
        
        // Always audit the error
        auditCommandError(command, context, error, recoveryStrategy);
    }
    
    private void handleRetryableError(Command<?> command, CommandExecutionContext context, Exception error) {
        var retryPolicy = determineRetryPolicy(command, error);
        
        if (retryService.canRetry(command, retryPolicy)) {
            log.info("Scheduling retry for command: {} due to transient error", command.getCommandType());
            retryService.scheduleRetry(command, retryPolicy);
        } else {
            log.warn("Retry limit exceeded for command: {}, moving to dead letter queue", command.getCommandType());
            deadLetterService.moveToDeadLetter(command, error, "RETRY_LIMIT_EXCEEDED");
        }
    }
    
    private void handleUserCorrectionRequired(Command<?> command, CommandExecutionContext context, Exception error) {
        // Extract actionable error information for user
        var userError = extractUserActionableError(error);
        
        // Store command state for potential retry after correction
        commandStateService.saveForUserCorrection(command, userError);
        
        // Notify user through appropriate channel
        notificationService.notifyUserCorrection(
            command.getExecutingUser(),
            command.getChannel(),
            userError
        );
    }
    
    private void handleManualIntervention(Command<?> command, CommandExecutionContext context, Exception error) {
        // Create manual intervention ticket
        var ticket = ManualInterventionTicket.builder()
            .command(command)
            .error(error)
            .context(context.getExecutionSummary())
            .priority(determinePriority(error))
            .assignedTeam(determineResponsibleTeam(command))
            .build();
            
        manualInterventionService.createTicket(ticket);
        
        // Notify operations team
        notificationService.notifyOperationsTeam(ticket);
    }
}

// Retry service with exponential backoff
@Component
public class CommandRetryService {
    
    private final TaskScheduler taskScheduler;
    private final Map<String, RetryAttempt> retryTracking = new ConcurrentHashMap<>();
    
    public boolean canRetry(Command<?> command, RetryPolicy policy) {
        var retryKey = generateRetryKey(command);
        var attempt = retryTracking.get(retryKey);
        return attempt == null || attempt.getAttemptCount() < policy.getMaxAttempts();
    }
    
    public void scheduleRetry(Command<?> command, RetryPolicy policy) {
        var retryKey = generateRetryKey(command);
        var attempt = retryTracking.computeIfAbsent(retryKey, k -> new RetryAttempt());
        
        attempt.incrementAttempt();
        var delay = policy.calculateDelay(attempt.getAttemptCount());
        
        taskScheduler.schedule(
            () -> retryCommand(command, retryKey),
            Instant.now().plus(delay)
        );
        
        log.info("Scheduled retry #{} for command {} in {} seconds", 
            attempt.getAttemptCount(), command.getCommandType(), delay.getSeconds());
    }
    
    private void retryCommand(Command<?> command, String retryKey) {
        try {
            commandProcessor.processCommand(command);
            // Success - remove from retry tracking
            retryTracking.remove(retryKey);
        } catch (Exception e) {
            // Retry failed - let error handler decide next step
            commandErrorRecoveryService.handleCommandError(command, null, e);
        }
    }
}
```

#### 6.4.3 Circuit Breaker Integration

**Command-Level Circuit Breaking:**
```java
@Component
public class CommandCircuitBreakerService {
    
    private final Map<String, CircuitBreaker> circuitBreakers = new ConcurrentHashMap<>();
    
    public <T> T executeWithCircuitBreaker(Command<T> command, Supplier<T> execution) {
        var circuitBreaker = getOrCreateCircuitBreaker(command);
        
        return circuitBreaker.executeSupplier(() -> {
            try {
                return execution.get();
            } catch (ExternalSystemException e) {
                // Record failures for external systems
                throw e;
            } catch (Exception e) {
                // Don't trip circuit breaker for business rule violations
                if (e instanceof BusinessRuleViolationException) {
                    throw e;
                }
                throw new CircuitBreakerException("Command execution failed", e);
            }
        });
    }
    
    private CircuitBreaker getOrCreateCircuitBreaker(Command<?> command) {
        var breakerKey = generateBreakerKey(command);
        return circuitBreakers.computeIfAbsent(breakerKey, key -> {
            var config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50) // Open if 50% of calls fail
                .waitDurationInOpenState(Duration.ofMinutes(1))
                .slidingWindowSize(10)
                .minimumNumberOfCalls(5)
                .build();
                
            return CircuitBreaker.of(key, config);
        });
    }
    
    private String generateBreakerKey(Command<?> command) {
        // Create circuit breaker per command type and external system
        return command.getCommandType() + ":" + command.getTargetModule();
    }
}
```

### 6.5 Command Audit & Traceability

#### 6.5.1 Comprehensive Audit Framework

**Audit Event Model:**
```java
@Entity
@Table(name = "command_audit_log")
public class CommandAuditRecord {
    @Id
    private String auditId;
    
    @Column(name = "correlation_id")
    private String correlationId;
    
    @Column(name = "command_type")
    @Enumerated(EnumType.STRING)
    private CommandType commandType;
    
    @Column(name = "journey_id")
    private CustomerJourneyId journeyId;
    
    @Column(name = "executing_user")
    private UserId executingUser;
    
    @Column(name = "channel")
    @Enumerated(EnumType.STRING)
    private ChannelType channel;
    
    @Column(name = "execution_start")
    private LocalDateTime executionStart;
    
    @Column(name = "execution_end")
    private LocalDateTime executionEnd;
    
    @Column(name = "execution_duration_ms")
    private Long executionDurationMs;
    
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CommandExecutionStatus status;
    
    @Column(name = "error_code")
    private String errorCode;
    
    @Column(name = "error_message")
    private String errorMessage;
    
    @Column(name = "command_payload", columnDefinition = "CLOB")
    private String commandPayload;
    
    @Column(name = "result_payload", columnDefinition = "CLOB")
    private String resultPayload;
    
    @Column(name = "execution_context", columnDefinition = "CLOB")
    private String executionContext;
    
    // Builder pattern for complex audit record creation
    public static CommandAuditRecord.Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        // Builder implementation for audit record construction
    }
}

// Audit service implementation
@Service
@Async
public class CommandAuditService {
    
    private final CommandAuditRepository auditRepository;
    private final AuditEventPublisher auditEventPublisher;
    
    public void auditCommandExecution(Command<?> command, CommandExecutionContext context) {
        var auditRecord = CommandAuditRecord.builder()
            .correlationId(command.getCorrelationId())
            .commandType(command.getCommandType())
            .journeyId(command.getJourneyId())
            .executingUser(command.getExecutingUser())
            .channel(command.getChannel())
            .executionStart(context.getStartTime())
            .executionEnd(context.getEndTime())
            .executionDurationMs(context.getExecutionDuration().toMillis())
            .status(context.isSuccessful() ? CommandExecutionStatus.SUCCESS : CommandExecutionStatus.FAILED)
            .commandPayload(sanitizeForAudit(command))
            .resultPayload(sanitizeForAudit(context.getResult()))
            .executionContext(JsonUtils.toJson(context.getExecutionSummary()))
            .build();
            
        if (!context.isSuccessful()) {
            auditRecord.setErrorCode(context.getError().getClass().getSimpleName());
            auditRecord.setErrorMessage(context.getError().getMessage());
        }
        
        // Persist audit record
        auditRepository.save(auditRecord);
        
        // Publish audit event for real-time monitoring
        auditEventPublisher.publish(new CommandAuditEvent(auditRecord));
    }
    
    private String sanitizeForAudit(Object payload) {
        if (payload == null) return null;
        
        // Remove sensitive information before auditing
        var sanitized = SensitiveDataSanitizer.sanitize(payload);
        return JsonUtils.toJson(sanitized);
    }
}
```

#### 6.5.2 Performance Monitoring Integration

**Command Performance Tracking:**
```java
@Component
public class CommandPerformanceMonitor {
    
    private final MeterRegistry meterRegistry;
    private final Timer.Builder commandTimerBuilder;
    private final Counter.Builder commandCounterBuilder;
    
    public CommandPerformanceMonitor(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.commandTimerBuilder = Timer.builder("command.execution.time")
            .description("Command execution time");
        this.commandCounterBuilder = Counter.builder("command.execution.count")
            .description("Command execution count");
    }
    
    public void recordCommandExecution(Command<?> command, CommandExecutionContext context) {
        var tags = Tags.of(
            "command.type", command.getCommandType().toString(),
            "module", command.getTargetModule().toString(),
            "channel", command.getChannel().toString(),
            "status", context.isSuccessful() ? "success" : "failed"
        );
        
        // Record execution time
        commandTimerBuilder
            .tags(tags)
            .register(meterRegistry)
            .record(context.getExecutionDuration());
            
        // Record execution count
        commandCounterBuilder
            .tags(tags)
            .register(meterRegistry)
            .increment();
            
        // Record error details if failed
        if (!context.isSuccessful()) {
            var errorTags = tags.and("error.type", context.getError().getClass().getSimpleName());
            Counter.builder("command.execution.errors")
                .tags(errorTags)
                .register(meterRegistry)
                .increment();
        }
    }
    
    public void recordCommandQueueMetrics(CommandType commandType, int queueSize, int processingTime) {
        Gauge.builder("command.queue.size")
            .tag("command.type", commandType.toString())
            .register(meterRegistry, queueSize);
            
        Timer.builder("command.queue.processing.time")
            .tag("command.type", commandType.toString())
            .register(meterRegistry)
            .record(Duration.ofMillis(processingTime));
    }
}
```

#### 6.5.3 Real-Time Command Monitoring

**Command Execution Dashboard:**
```java
@RestController
@RequestMapping("/api/monitoring/commands")
public class CommandMonitoringController {
    
    private final CommandAuditService auditService;
    private final CommandPerformanceService performanceService;
    
    @GetMapping("/execution-stats")
    public CommandExecutionStats getExecutionStats(
            @RequestParam(required = false) CommandType commandType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since) {
        
        var criteria = CommandStatsCriteria.builder()
            .commandType(commandType)
            .since(since != null ? since : LocalDateTime.now().minusHours(24))
            .build();
            
        return performanceService.getExecutionStats(criteria);
    }
    
    @GetMapping("/active-commands")
    public List<ActiveCommandInfo> getActiveCommands() {
        return commandTrackingService.getActiveCommands()
            .stream()
            .map(this::toActiveCommandInfo)
            .collect(Collectors.toList());
    }
    
    @GetMapping("/failed-commands")
    public Page<FailedCommandInfo> getFailedCommands(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        var pageable = PageRequest.of(page, size);
        return auditService.getFailedCommands(pageable)
            .map(this::toFailedCommandInfo);
    }
    
    private ActiveCommandInfo toActiveCommandInfo(CommandExecutionContext context) {
        return ActiveCommandInfo.builder()
            .commandType(context.getCommand().getCommandType())
            .journeyId(context.getCommand().getJourneyId())
            .executingUser(context.getCommand().getExecutingUser())
            .startTime(context.getStartTime())
            .currentDuration(Duration.between(context.getStartTime(), LocalDateTime.now()))
            .build();
    }
}
```

---

*This command-driven architecture implementation provides a robust foundation for handling complex business workflows while maintaining clear separation of concerns, comprehensive error handling, and full auditability. The pattern scales well as new commands and modules are added to the system.*


## 7. Event-Driven Coordination

### 7.1 Domain Event Design & Contracts

#### 7.1.1 Event Design Philosophy

**Event-First Domain Modeling:**
The architecture treats domain events as first-class citizens that represent meaningful business state changes. Events serve dual purposes: coordinating workflow progression and providing audit trails for regulatory compliance. Each event captures a specific business fact that occurred, enabling loose coupling between modules while maintaining strong consistency within transaction boundaries.

**Event Characteristics:**
- **Immutable**: Events represent facts that have already occurred
- **Self-Contained**: All necessary information included in event payload
- **Business-Meaningful**: Events reflect domain language and business processes
- **Correlation-Enabled**: Include journey and correlation identifiers for tracing
- **Versioned**: Support schema evolution without breaking consumers

#### 7.1.2 Event Contract Design

**Hierarchical Event Structure:**
```java
// Base event with common infrastructure concerns
public abstract class DomainEvent {
    private final String eventId;
    private final CustomerJourneyId journeyId;
    private final LocalDateTime timestamp;
    private final String correlationId;
    private final EventVersion version;
}

// Module-specific event families
public abstract class DataProcurementEvent extends DomainEvent {
    private final DataProcurementId dataProcurementId;
}

public class DataProcurementCompletedEvent extends DataProcurementEvent {
    private final LocalDateTime completedAt;
    private final BigDecimal completionPercentage;
    private final List<ValidationWarning> warnings;
}
```

**Event Contract Principles:**
- **Backward Compatibility**: New fields optional, existing fields never removed
- **Forward Compatibility**: Consumers ignore unknown fields gracefully
- **Semantic Versioning**: Version changes indicate compatibility guarantees
- **Payload Minimization**: Only essential data included, avoid deep object graphs
- **Business Context**: Include sufficient context for downstream decision-making

#### 7.1.3 Event Taxonomy

**State Change Events:**
- **DataProcurementCompletedEvent**: Data gathering phase finished successfully
- **OfferingCalculatedEvent**: Pricing calculation completed with results
- **OfferingAcceptedEvent**: Customer accepted specific coverage options
- **PaymentSuccessfulEvent**: Payment processing completed successfully
- **PolicyCreatedEvent**: Insurance policy created in TIA system

**Process Events:**
- **DataProcurementStartedEvent**: New data procurement process initiated
- **OfferingRecalculatedEvent**: Pricing recalculated due to data changes
- **CheckoutInitializedEvent**: Customer ready to proceed with payment

**Error Events:**
- **ValidationFailedEvent**: Business rule validation prevented progression
- **ExternalSystemFailureEvent**: TIA/Earnix integration issues
- **PaymentFailedEvent**: Payment processing unsuccessful

**Administrative Events:**
- **JourneyExpiredEvent**: Process exceeded maximum duration
- **ManualInterventionRequiredEvent**: Human review needed
- **ConcurrencyViolationEvent**: Conflicting access detected

### 7.2 Event Processing Pipeline (Spring Events)

#### 7.2.1 Synchronous Event Processing Architecture

**Design Rationale:**
The architecture uses Spring's synchronous ApplicationEventPublisher to ensure events are processed within the same transaction as the triggering command. This choice prioritizes data consistency over performance, ensuring that workflow state changes are atomic and immediately consistent.

**Transaction Boundary Strategy:**
All event processing occurs within the originating transaction, meaning:
- Event handlers participate in the same database transaction
- If any event handler fails, the entire operation rolls back
- No eventually consistent states during normal operation
- Simpler error handling and debugging

**Event Processing Flow:**
1. Domain service executes business logic and persists state changes
2. Domain service publishes event using ApplicationEventPublisher
3. Spring routes event to all registered @EventListener methods
4. Event handlers execute synchronously within same transaction
5. Transaction commits only if all handlers succeed
6. Rollback occurs if any handler fails

#### 7.2.2 Event Handler Organization

**Handler Registration Patterns:**
Event handlers are organized by their primary concern rather than by module boundaries. This enables clear separation between workflow coordination (CustomerJourney module) and module-specific reactions.

**CustomerJourney Event Handlers:**
- **Workflow Progression**: Determine next steps in sales process
- **State Synchronization**: Update journey state based on module events
- **Cross-Module Coordination**: Trigger commands on other modules
- **Business Rule Enforcement**: Apply workflow transition rules

**Module-Specific Event Handlers:**
- **Internal Reactions**: Module responds to its own events
- **Cache Management**: Update cached data based on state changes
- **External Notifications**: Inform external systems of state changes
- **Audit Trail**: Record module-specific audit information

#### 7.2.3 Event Handler Priority and Ordering

**Handler Execution Sequence:**
Spring provides @Order annotation to control handler execution sequence. The architecture establishes clear precedence:

1. **Audit Handlers** (Order 1): Record events before any processing
2. **State Synchronization** (Order 10): Update journey state immediately
3. **Workflow Coordination** (Order 20): Determine next process steps
4. **Module Reactions** (Order 30): Module-specific processing
5. **External Notifications** (Order 40): Inform external systems
6. **Cache Updates** (Order 50): Update cached data last

**Critical Handler Protection:**
Essential handlers (audit, state synchronization) execute first to ensure they complete even if later handlers fail. This guarantees that critical business state is preserved even during partial failures.

### 7.3 Event-to-Journey Correlation Strategy

#### 7.3.1 Correlation Architecture

**Journey ID Propagation:**
Every event includes the CustomerJourneyId that originated the business process. This enables event handlers to locate the appropriate journey context without complex lookups or mapping tables.

**Correlation Benefits:**
- **Direct Lookup**: Find journey using event.getJourneyId()
- **Audit Trail**: Complete event history per journey
- **Debugging**: Trace all events for specific customer case
- **Performance**: No additional database joins required
- **Simplicity**: Avoids complex correlation mapping logic

**Design Trade-offs:**
The architecture accepts slight coupling (events know about journeys) for significant operational benefits. This pragmatic approach reduces complexity while enabling effective debugging and audit trails.

#### 7.3.2 Event Routing Mechanisms

**Topic-Based Routing:**
Events are routed based on event type rather than destination. Event listeners declare interest in specific event types using @EventListener, enabling loose coupling and dynamic handler registration.

**Handler Discovery:**
Spring automatically discovers event handlers at startup, reducing configuration overhead. New handlers can be added without modifying existing code or configuration.

**Conditional Event Processing:**
Handlers can include conditional logic to process events only when relevant:

```java
@EventListener(condition = "#event.isSignificantChange()")
public void onDataChanged(DataProcurementDataChangedEvent event) {
    // Only process events that require downstream actions
}
```

#### 7.3.3 Context Enrichment Strategy

**Event Context Design:**
Events include sufficient context for handlers to make processing decisions without additional database queries. This reduces coupling and improves performance.

**Context Information Levels:**
- **Minimal**: Event type, journey ID, timestamp
- **Standard**: Above plus entity ID, status, user context
- **Rich**: Above plus relevant business data for decision-making
- **Complete**: Full entity snapshot (avoided due to size/coupling concerns)

### 7.4 Event Ordering & Idempotency

#### 7.4.1 Event Ordering Guarantees

**Within-Transaction Ordering:**
Events published within the same transaction are processed in publication order due to synchronous processing. This ensures predictable state transitions and workflow progression.

**Cross-Transaction Ordering:**
The architecture makes no guarantees about event ordering across different transactions. This design choice accepts potential race conditions for operational simplicity.

**Ordering Requirements Analysis:**
Most business scenarios don't require strict cross-transaction ordering:
- Customer data updates are infrequent and typically single-user
- Pricing calculations are idempotent with respect to input data
- Payment processing uses external idempotency mechanisms

#### 7.4.2 Idempotency Patterns

**Natural Idempotency:**
Many event handlers are naturally idempotent due to business logic:
- State updates check current state before modification
- Cache invalidation is idempotent by nature
- Audit records include event IDs to prevent duplicates

**Explicit Idempotency Controls:**
For handlers that require explicit idempotency protection:

```java
@EventListener
public void onOfferingCalculated(OfferingCalculatedEvent event) {
    // Check if already processed
    if (hasProcessedEvent(event.getEventId())) {
        return; // Skip duplicate processing
    }
    
    // Process event and mark as processed
    processOffering(event);
    markEventProcessed(event.getEventId());
}
```

**Database-Level Idempotency:**
Unique constraints and upsert operations provide database-level idempotency protection for critical operations.

#### 7.4.3 Event Deduplication Strategy

**Event Identity Design:**
Each event includes a unique eventId (UUID) generated at creation time. This identifier enables reliable deduplication across the system.

**Deduplication Mechanisms:**
- **Handler-Level**: Handlers track processed event IDs
- **Database-Level**: Unique constraints prevent duplicate operations
- **Application-Level**: Event processors check for duplicate handling

**Performance Considerations:**
Deduplication tracking uses efficient data structures and cleanup processes to prevent memory/storage bloat from accumulating event IDs.

### 7.5 Failure Handling & Dead Letter Processing

#### 7.5.1 Failure Classification Framework

**Failure Categories:**
The architecture classifies event processing failures into distinct categories requiring different handling strategies:

**Transient Failures:**
- Database connection timeouts
- External service temporary unavailability
- Resource contention issues
- Recovery: Automatic retry with exponential backoff

**Business Rule Failures:**
- Validation errors during event processing
- Invalid state transitions
- Business logic violations
- Recovery: Manual correction or process override

**System Failures:**
- Programming errors in event handlers
- Configuration issues
- Data corruption
- Recovery: Code fixes and event replay

**Integration Failures:**
- TIA system unavailability
- Network connectivity issues
- External service errors
- Recovery: Circuit breaker pattern and manual intervention

#### 7.5.2 Error Recovery Mechanisms

**Immediate Recovery Strategies:**
Within the synchronous processing model, failures can be handled immediately:

**Transaction Rollback:**
When critical event handlers fail, the entire transaction rolls back, maintaining consistency. The originating command fails, allowing the user to retry the operation.

**Graceful Degradation:**
Non-critical event handlers can catch exceptions and log errors without failing the transaction, allowing core business operations to continue.

**Conditional Processing:**
Event handlers can defer processing when dependencies are unavailable, marking items for later retry.

#### 7.5.3 Dead Letter Queue Design

**Dead Letter Requirements:**
Although the current architecture uses synchronous processing, the design anticipates evolution toward asynchronous processing for non-critical events.

**Dead Letter Queue Characteristics:**
- **Event Preservation**: Complete event data and context
- **Failure Analysis**: Detailed error information and stack traces
- **Replay Capability**: Ability to reprocess events after fixes
- **Monitoring Integration**: Alerts and dashboards for failed events

**Dead Letter Processing:**
```java
@Component
public class DeadLetterProcessor {
    
    public void moveToDeadLetter(DomainEvent event, Exception failure, String reason) {
        var deadLetterRecord = DeadLetterRecord.builder()
            .originalEvent(event)
            .failureReason(reason)
            .failureDetails(ExceptionUtils.getStackTrace(failure))
            .timestamp(LocalDateTime.now())
            .retryCount(0)
            .build();
            
        deadLetterRepository.save(deadLetterRecord);
        notifyOperationsTeam(deadLetterRecord);
    }
}
```

#### 7.5.4 Event Replay Architecture

**Replay Requirements:**
The system must support event replay for recovery scenarios:
- After fixing bugs in event handlers
- During system recovery after outages
- For testing new event handler logic

**Replay Design Patterns:**
- **Temporal Replay**: Replay all events from specific time period
- **Selective Replay**: Replay specific event types or journey events
- **Compensating Replay**: Apply corrective events to fix inconsistent state

**Replay Safety:**
Event handlers must be designed for safe replay:
- Idempotent operations that can be safely repeated
- State checks before applying changes
- Proper handling of already-processed events

#### 7.5.5 Monitoring and Alerting

**Event Processing Metrics:**
- Event processing latency per event type
- Event handler success/failure rates
- Dead letter queue depth and growth rate
- Event replay frequency and success rates

**Alert Conditions:**
- Event handler failure rate exceeds threshold
- Dead letter queue accumulation
- Event processing latency degradation
- Critical business events missing from audit trail

**Operational Dashboards:**
Real-time visibility into event processing health:
- Event flow visualizations
- Handler performance metrics
- Error trending and analysis
- Journey completion rates by event path

---

*This event-driven coordination architecture provides loose coupling between modules while maintaining strong consistency guarantees. The design balances simplicity with scalability, enabling future evolution toward more sophisticated event processing patterns as business requirements grow.*


## 8. State Management & Persistence

### 8.1 Workflow State Design Patterns

#### 8.1.1 Aggregate Root Pattern Implementation

**CustomerJourney as Aggregate Root:**
The CustomerJourney entity serves as the aggregate root for the entire sales workflow, centralizing state management and ensuring consistency across all business process phases. This design pattern provides a single entry point for state modifications while maintaining clear boundaries around related entities.

**Aggregate Boundary Definition:**
The aggregate boundary encompasses the complete sales workflow state but intentionally excludes the rich domain models within each module. This separation allows modules to maintain their own complex internal state while exposing only essential coordination information to the workflow orchestrator.

**State Encapsulation Strategy:**
```java
// Aggregate root manages minimal state references
@Entity
public class CustomerJourney {
    // Core workflow state
    private WorkflowStep currentStep;
    private ProductVersion productVersion;
    
    // Minimal module state references
    @Embedded private DataProcurementState dataProcurementState;
    @Embedded private OfferingState offeringState;
    @Embedded private CheckoutState checkoutState;
    
    // Workflow coordination methods ensure state consistency
    public void transitionTo(WorkflowStep newStep) {
        validateTransition(currentStep, newStep);
        this.currentStep = newStep;
        recordStateChange();
    }
}
```

**Design Benefits:**
- **Consistency Boundary**: All workflow state changes occur within single transaction
- **Encapsulation**: Internal module complexity hidden from workflow coordination
- **Performance**: Minimal state loading for workflow decisions
- **Scalability**: Aggregate size remains bounded regardless of module complexity

#### 8.1.2 State Value Object Design

**Immutable State Representation:**
Module states are represented as immutable value objects that capture essential information for workflow coordination without exposing internal module complexity. This approach provides clean boundaries while enabling efficient state querying.

**Value Object Characteristics:**
- **Immutable**: State objects never change after creation
- **Self-Contained**: Include all information needed for workflow decisions
- **Minimal**: Only essential coordination data, not full domain state
- **Versionable**: Support evolution without breaking existing workflows

**State Object Examples:**
```java
@Embeddable
public record DataProcurementState(
    DataProcurementId id,
    boolean isComplete,
    LocalDateTime completedAt,
    BigDecimal completionPercentage
) {
    public boolean isReadyForOffering() {
        return isComplete && completionPercentage.equals(BigDecimal.valueOf(100));
    }
}

@Embeddable  
public record OfferingState(
    OfferingId id,
    OfferingStatus status,
    ProductVersion calculatedVersion,
    boolean customerAccepted,
    LocalDateTime expiresAt
) {
    public boolean needsRecalculation(ProductVersion currentVersion) {
        return !calculatedVersion.equals(currentVersion) || isExpired();
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
```

#### 8.1.3 State Machine Implementation

**Explicit State Transitions:**
The workflow implements an explicit state machine pattern where valid transitions are defined declaratively rather than scattered throughout business logic. This approach provides clear visibility into business process flows and prevents invalid state transitions.

**State Machine Architecture:**
```java
public enum WorkflowStep {
    DATA_PROCUREMENT {
        @Override
        public Set<WorkflowStep> getAllowedTransitions() {
            return Set.of(OFFERING, DATA_PROCUREMENT); // Can retry or proceed
        }
        
        @Override
        public boolean canTransitionTo(WorkflowStep target) {
            return getAllowedTransitions().contains(target);
        }
    },
    OFFERING {
        @Override  
        public Set<WorkflowStep> getAllowedTransitions() {
            return Set.of(DATA_PROCUREMENT, CHECKOUT, OFFERING);
        }
    }
    // Additional states with transition rules
}
```

**State Machine Benefits:**
- **Business Rule Enforcement**: Invalid transitions prevented at compile time
- **Documentation**: State machine serves as living process documentation
- **Testing**: State transitions can be tested independently
- **Debugging**: Clear visibility into process flow and current state

### 8.2 Entity Models & Relationships

#### 8.2.1 Entity Relationship Architecture

**Hierarchical Entity Design:**
The persistence model reflects the domain hierarchy with CustomerJourney as the root entity containing embedded value objects for module states. This design minimizes database joins while maintaining clear ownership boundaries.

**Relationship Patterns:**
- **Composition**: CustomerJourney contains module state value objects
- **Association**: Modules contain rich entities referenced by ID from journey
- **Aggregation**: Shared entities (Customer, Product) referenced across modules

**Database Schema Strategy:**
```sql
-- Core workflow table
customer_journeys (
    id,
    customer_id,
    current_step,
    product_version,
    data_procurement_id,        -- Reference to module entity
    data_procurement_complete,  -- Embedded state
    offering_id,               -- Reference to module entity  
    offering_status,           -- Embedded state
    checkout_id,               -- Reference to module entity
    payment_succeeded          -- Embedded state
)

-- Module-specific rich entities
data_procurements (
    id,
    customer_journey_id,       -- Back reference for queries
    customer_data,             -- Rich domain data
    validation_results,        -- Complex module state
    completion_percentage
)
```

#### 8.2.2 Persistence Context Management

**Transaction Boundary Design:**
The architecture uses single transaction boundaries for command execution to ensure consistency between workflow state updates and module operations. This approach prioritizes data consistency over performance scalability.

**Entity Loading Strategy:**
- **Lazy Loading**: Module entities loaded only when needed for business operations
- **Eager Loading**: Workflow state always loaded for coordination decisions
- **Selective Loading**: Query-specific projections for reporting and dashboard needs

**Optimistic Locking Implementation:**
```java
@Entity
public class CustomerJourney {
    @Version
    private Long version;  // JPA optimistic locking
    
    @Column(name = "last_modified")
    private LocalDateTime lastModified;
    
    @Column(name = "last_modified_by") 
    private UserId lastModifiedBy;
    
    // Business-level concurrency control
    public boolean canBeModifiedBy(UserId userId, ChannelType channel) {
        return lastModifiedBy.equals(userId) || 
               lastModified.isBefore(LocalDateTime.now().minusHours(2));
    }
}
```

#### 8.2.3 Data Consistency Patterns

**Consistency Guarantees:**
The system provides different consistency guarantees based on business requirements:

**Strong Consistency (ACID):**
- Workflow state transitions within single journey
- Payment processing and policy creation
- Critical business rule validation

**Eventual Consistency:**
- Cross-journey reporting and analytics
- Cache updates and derived data
- External system synchronization

**Consistency Implementation:**
- **Database Transactions**: ACID properties for critical operations
- **Application Transactions**: @Transactional spanning multiple operations
- **Event-Driven Updates**: Eventual consistency for non-critical data

### 8.3 State Transition Logic & Validation

#### 8.3.1 Transition Validation Framework

**Multi-Level Validation:**
State transitions undergo validation at multiple levels to ensure business rule compliance and data integrity:

**Structural Validation:**
- Required fields present and valid format
- State object integrity constraints
- Database constraint compliance

**Business Rule Validation:**
- Workflow step transition rules
- Module-specific business constraints
- Cross-module consistency requirements

**Temporal Validation:**
- Process expiration policies (7-day limit)
- Offering validity periods
- Product version compatibility

**Validation Implementation Strategy:**
```java
public class StateTransitionValidator {
    
    public ValidationResult validateTransition(
            CustomerJourney journey, 
            WorkflowStep targetStep,
            Object transitionContext) {
        
        var results = new ArrayList<ValidationResult>();
        
        // 1. Structural validation
        results.add(validateStructuralConstraints(journey, targetStep));
        
        // 2. Business rule validation  
        results.add(validateBusinessRules(journey, targetStep, transitionContext));
        
        // 3. Temporal validation
        results.add(validateTemporalConstraints(journey, targetStep));
        
        return ValidationResult.aggregate(results);
    }
}
```

#### 8.3.2 Conditional State Transitions

**Context-Dependent Transitions:**
Some state transitions depend on external factors or business conditions that must be evaluated at runtime:

**Channel-Specific Rules:**
Different transition rules apply based on the sales channel (agent vs call center vs online), reflecting varying business processes and authorization levels.

**Product-Specific Rules:**
Certain insurance products may require additional steps or validations not present in standard motor insurance workflows.

**Customer-Specific Rules:**
High-risk customers or special cases may require manual underwriting steps that alter the standard workflow progression.

**Dynamic Transition Logic:**
```java
public class WorkflowProgressionService {
    
    public boolean shouldAutoProgressToOffering(CustomerJourney journey) {
        // Business logic determines automatic progression
        return journey.isDataProcurementComplete() &&
               !requiresManualReview(journey) &&
               hasValidProductVersion(journey);
    }
    
    private boolean requiresManualReview(CustomerJourney journey) {
        // Complex business rules for manual intervention
        return isHighRiskCustomer(journey) || 
               hasComplexVehicleConfiguration(journey) ||
               requiresSpecialUnderwriting(journey);
    }
}
```

#### 8.3.3 State Consistency Enforcement

**Invariant Protection:**
The system enforces business invariants through multiple mechanisms:

**Database Constraints:**
- Foreign key relationships ensure referential integrity
- Check constraints enforce basic business rules
- Unique constraints prevent duplicate states

**Application-Level Constraints:**
- Business rule validation in entity methods
- State machine transition enforcement
- Cross-module consistency checks

**Event-Driven Consistency:**
- Compensating actions for failed state changes
- Cross-module state synchronization
- Audit trail for consistency verification

### 8.4 Versioning & Backward Compatibility

#### 8.4.1 Schema Evolution Strategy

**Version-Safe Entity Design:**
Entities are designed to accommodate schema changes without breaking existing functionality or requiring complex migrations:

**Additive Changes:**
- New optional fields added with default values
- Additional enum values for extensibility
- Supplementary tables for new functionality

**Non-Breaking Modifications:**
- Field size increases (varchar expansion)
- Index additions for performance
- Constraint relaxation where business-appropriate

**Breaking Change Management:**
- Deprecation periods for removed functionality
- Migration scripts for data transformation
- Compatibility layers for transition periods

#### 8.4.2 Product Version Management

**Version Locking Mechanism:**
Each customer journey locks to a specific product version at initiation, ensuring pricing and rule consistency throughout the potentially long-running sales process.

**Version Compatibility Framework:**
```java
@Embeddable
public class ProductVersion {
    private String major;
    private String minor; 
    private String patch;
    
    public boolean isCompatibleWith(ProductVersion other) {
        // Semantic versioning compatibility rules
        return major.equals(other.major) && 
               Integer.parseInt(minor) >= Integer.parseInt(other.minor);
    }
    
    public boolean requiresRecalculation(ProductVersion current) {
        // Business rules for when recalculation is needed
        return !major.equals(current.major) ||
               Integer.parseInt(minor) < Integer.parseInt(current.minor);
    }
}
```

**Version Management Benefits:**
- **Price Guarantees**: Customers get prices based on rules active when quoted
- **Regulatory Compliance**: Rate filings remain consistent during sales process
- **Business Continuity**: Long-running processes unaffected by product updates
- **Audit Trail**: Clear record of which rules applied to each transaction

#### 8.4.3 Data Migration Patterns

**Zero-Downtime Migration Strategy:**
The architecture supports data migrations without system downtime through careful planning and execution:

**Dual-Write Pattern:**
During migration periods, writes go to both old and new schemas while reads transition gradually, ensuring no data loss during schema evolution.

**Shadow Table Migration:**
Large data migrations use shadow tables and background processes to transform data while the system remains operational.

**Feature Toggle Migration:**
New schema features are protected by feature toggles, allowing gradual rollout and quick rollback if issues arise.

### 8.5 State Reset & Cascade Operations

#### 8.5.1 Cascade Reset Architecture

**State Dependency Management:**
When upstream data changes, downstream states must be reset to maintain consistency. The architecture implements explicit cascade rules rather than relying on database cascades.

**Reset Cascade Rules:**
- **Data Procurement Changes**: Reset offering and checkout states
- **Offering Changes**: Reset checkout state only
- **Checkout Changes**: No downstream resets (terminal operations)

**Cascade Implementation:**
```java
public class CustomerJourney {
    
    public void updateDataProcurementState(DataProcurementState newState) {
        var oldState = this.dataProcurementState;
        this.dataProcurementState = newState;
        
        // Cascade reset if data actually changed
        if (isSignificantChange(oldState, newState)) {
            resetOfferingState("Data procurement changed");
            resetCheckoutState("Upstream data changed");
            recordCascadeReset();
        }
    }
    
    private void resetOfferingState(String reason) {
        this.offeringState = null;
        publishEvent(new OfferingStateResetEvent(id, reason));
    }
}
```

#### 8.5.2 Partial State Recovery

**Granular Reset Operations:**
The system supports partial state resets that preserve valid work while clearing affected components:

**Selective Reset Strategies:**
- **Field-Level Reset**: Clear specific data elements while preserving others
- **Module-Level Reset**: Reset entire module state while preserving workflow position
- **Time-Based Reset**: Reset changes after specific timestamp

**Recovery Optimization:**
Rather than forcing complete restart, the system attempts to preserve maximum valid state:

- Customer demographic data rarely needs reset
- Vehicle information stays valid unless VIN changes
- Insurance preferences preserved across most data changes

#### 8.5.3 State Reset Audit Trail

**Reset Tracking Requirements:**
All state resets are tracked for audit and debugging purposes:

**Reset Audit Information:**
- **What**: Which states were reset and why
- **When**: Timestamp of reset operation
- **Who**: User or system component that triggered reset
- **Impact**: Downstream effects and affected processes

**Reset Event Framework:**
```java
public class StateResetEvent extends DomainEvent {
    private final String resetReason;
    private final Set<String> affectedStates;
    private final UserId triggeredBy;
    private final ResetScope scope;
    
    // Reset events trigger compensating actions
    // and notify affected systems
}
```

#### 8.5.4 State Recovery Mechanisms

**Automatic Recovery Patterns:**
The system implements automatic recovery for certain types of state inconsistencies:

**Self-Healing Operations:**
- **Validation Refresh**: Re-run validation when external rules change
- **Cache Regeneration**: Rebuild derived state from authoritative sources
- **Reference Updates**: Update external system references automatically

**Manual Recovery Support:**
For complex inconsistencies requiring human intervention:

**Recovery Tooling:**
- Administrative interfaces for state inspection
- Guided recovery workflows for support staff
- State export/import for complex debugging scenarios

**Recovery Documentation:**
- Runbooks for common recovery scenarios
- Escalation procedures for complex state issues
- Knowledge base of past recovery operations

---

*This state management and persistence architecture provides robust data consistency while supporting the complex business requirements of long-running insurance sales processes. The design balances consistency guarantees with operational flexibility, enabling both automatic workflow progression and manual intervention when needed.*

## 9. Transaction Management & Data Consistency

### 9.1 Transaction Boundary Design

#### 9.1.1 Command-Centric Transaction Boundaries

**Single Command, Single Transaction:**
The architecture establishes transaction boundaries at the command level, ensuring that each business command executes within a single database transaction. This design provides strong consistency guarantees while simplifying error handling and recovery scenarios.

**Transaction Scope Definition:**
Each transaction encompasses:
- Command validation and security checks
- Business logic execution within target module
- State updates in CustomerJourney aggregate
- Immediate event processing and coordination
- Audit trail creation and compliance logging

**Boundary Enforcement Strategy:**
Transaction boundaries are enforced through Spring's @Transactional annotation at the service layer, with careful consideration of propagation behaviors to ensure consistent behavior across module interactions.

**Design Benefits:**
- **ACID Guarantees**: All related changes commit or rollback together
- **Simplified Error Handling**: Single failure point for entire business operation
- **Consistency Assurance**: No intermediate inconsistent states visible
- **Debugging Simplicity**: Clear transaction boundaries aid troubleshooting

#### 9.1.2 Cross-Module Transaction Coordination

**Orchestrator Transaction Management:**
The CustomerJourney module serves as the transaction coordinator for cross-module operations, ensuring that workflow state updates and module operations occur within the same transaction boundary.

**Module Participation Patterns:**
Modules participate in transactions initiated by the CustomerJourney orchestrator through:
- **REQUIRED Propagation**: Module operations join existing transaction
- **Command Interface Design**: Module commands execute within caller's transaction
- **Event Handler Coordination**: Event processing occurs within originating transaction

**Transaction Coordination Flow:**
```java
@Transactional  // Transaction boundary at orchestrator level
public DataProcurementResult startDataProcurement(StartDataProcurementCommand command) {
    // 1. Load journey state (participates in transaction)
    var journey = journeyRepository.findById(command.getJourneyId());
    
    // 2. Execute module command (participates in same transaction)
    var result = dataProcurementCommands.processCustomerData(context);
    
    // 3. Update journey state (participates in same transaction)
    journey.updateDataProcurementState(extractState(result));
    
    // 4. Event processing (participates in same transaction)
    // Events published and handled within same transaction
    
    return result; // All operations commit together
}
```

#### 9.1.3 Transaction Size Optimization

**Balanced Transaction Scope:**
Transaction boundaries are sized to balance consistency requirements with performance characteristics:

**Included in Transaction:**
- Core business operation execution
- Essential state updates and validations
- Critical event processing for workflow coordination
- Mandatory audit trail creation

**Excluded from Transaction:**
- Non-critical cache updates (eventual consistency acceptable)
- External system notifications (idempotent retry patterns)
- Analytics and reporting data updates
- Optional performance optimizations

**Transaction Performance Considerations:**
- **Database Lock Duration**: Minimize lock holding time through efficient operations
- **External System Calls**: Isolate slow external calls from transaction boundaries
- **Batch Operations**: Group related operations to reduce transaction overhead
- **Read Optimization**: Use appropriate isolation levels for read operations

### 9.2 Consistency Patterns (Immediate vs Eventual)

#### 9.2.1 Immediate Consistency Requirements

**Strong Consistency Domains:**
Certain business operations require immediate consistency to maintain data integrity and business rule compliance:

**Financial Operations:**
- Payment processing and policy creation must be atomic
- Premium calculations and customer commitments require immediate consistency
- Refund and cancellation operations need strong consistency guarantees

**Workflow State Management:**
- Journey state transitions must be immediately consistent
- Module state synchronization requires strong consistency
- Concurrency control depends on immediate state visibility

**Regulatory Compliance:**
- Audit trail creation must be immediately consistent with business operations
- Rate filing compliance requires immediate consistency with pricing rules
- Customer data handling must maintain immediate consistency for privacy compliance

#### 9.2.2 Eventual Consistency Domains

**Acceptable Eventual Consistency:**
Some system aspects can operate with eventual consistency without compromising business operations:

**Reporting and Analytics:**
- Management dashboards and business intelligence reports
- Performance metrics and operational monitoring data
- Historical trend analysis and predictive modeling

**Cache and Derived Data:**
- Customer lookup caches and search indexes
- Aggregated statistics and summary information
- Non-critical performance optimization data

**External System Synchronization:**
- Marketing system contact updates
- Document management system metadata
- Third-party analytics and tracking systems

**Eventual Consistency Implementation:**
```java
// Immediate consistency for core operation
@Transactional
public PolicyCreationResult createPolicy(CreatePolicyCommand command) {
    // Core operation with immediate consistency
    var policy = tiaAdapter.createPolicy(command.getOfferingData());
    journey.markPolicyCreated(policy.getPolicyNumber());
    
    // Eventual consistency for non-critical operations
    eventPublisher.publishEvent(new PolicyCreatedEvent(journey.getId(), policy));
    return PolicyCreationResult.success(policy);
}

// Eventual consistency handler (separate transaction)
@EventListener
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void updateCacheOnPolicyCreation(PolicyCreatedEvent event) {
    // Update search indexes, caches, etc.
    // Failure here doesn't affect core policy creation
}
```

#### 9.2.3 Consistency Level Decision Framework

**Consistency Requirement Analysis:**
The architecture provides a framework for determining appropriate consistency levels:

**Business Impact Assessment:**
- **High Impact**: Financial transactions, workflow state, regulatory data
- **Medium Impact**: Customer experience features, operational efficiency
- **Low Impact**: Analytics, caching, performance optimizations

**Failure Cost Analysis:**
- **High Cost**: Data corruption, compliance violations, financial errors
- **Medium Cost**: User experience degradation, operational inefficiency
- **Low Cost**: Delayed reporting, cache misses, performance impacts

**Recovery Complexity:**
- **Complex Recovery**: Manual intervention, data reconstruction, regulatory reporting
- **Medium Recovery**: Automated correction, background processing
- **Simple Recovery**: Cache refresh, eventual synchronization

### 9.3 Rollback & Recovery Mechanisms

#### 9.3.1 Automatic Rollback Scenarios

**Transaction Rollback Triggers:**
The system automatically rolls back transactions when specific conditions occur:

**Business Rule Violations:**
- Validation failures that prevent operation completion
- State transition violations in workflow processing
- Security violations or unauthorized access attempts

**Technical Failures:**
- Database constraint violations or integrity errors
- External system integration failures requiring consistency
- Application errors in critical business logic

**Rollback Implementation Strategy:**
```java
@Transactional(rollbackFor = {BusinessRuleException.class, IntegrationException.class})
public OfferingResult calculateOffering(CalculateOfferingCommand command) {
    try {
        // Business operations
        var offering = offeringService.calculate(command);
        journey.updateOfferingState(offering.getState());
        
        return offering;
    } catch (TiaIntegrationException e) {
        // Automatic rollback triggered
        // Journey state and offering calculation both rolled back
        throw new OfferingCalculationException("TIA integration failed", e);
    }
}
```

#### 9.3.2 Compensating Transaction Patterns

**Manual Recovery Operations:**
For scenarios where automatic rollback isn't sufficient, the system provides compensating transaction patterns:

**Partial Success Scenarios:**
When external systems accept operations that later need reversal due to business rule failures or customer requests.

**Cross-System Consistency:**
When operations span multiple external systems that don't support distributed transactions.

**Time-Delayed Failures:**
When failures occur after initial transaction success, requiring corrective actions.

**Compensation Implementation:**
```java
@Component
public class PolicyCompensationService {
    
    public void compensatePolicyCreation(PolicyNumber policyNumber, String reason) {
        // Create compensating transaction to reverse policy creation
        var compensationRequest = CompensationRequest.builder()
            .policyNumber(policyNumber)
            .reason(reason)
            .requestedBy(getCurrentUser())
            .build();
            
        // Execute compensation through TIA
        tiaAdapter.cancelPolicy(compensationRequest);
        
        // Update journey state to reflect compensation
        journey.markPolicyCompensated(policyNumber, reason);
        
        // Audit compensation action
        auditService.recordCompensation(compensationRequest);
    }
}
```

#### 9.3.3 Recovery Point Management

**Recovery Checkpoint Strategy:**
The system establishes recovery checkpoints at significant business milestones:

**Checkpoint Locations:**
- After successful data procurement completion
- Following offering calculation and customer acceptance
- Upon successful payment processing
- After policy creation in TIA system

**Checkpoint Implementation:**
Recovery checkpoints capture sufficient state to resume operations from stable points:

**Checkpoint Data:**
- Complete journey state at checkpoint time
- Module entity states and their relationships
- External system reference numbers and statuses
- User context and business decisions made

**Recovery Procedures:**
- **Automated Recovery**: System attempts automatic recovery to last checkpoint
- **Guided Recovery**: Support tools help staff recover to known good state
- **Manual Recovery**: Complex scenarios requiring expert intervention

### 9.4 Concurrency Control Implementation

#### 9.4.1 Optimistic Locking Strategy

**JPA Version-Based Locking:**
The system uses JPA's @Version annotation for optimistic concurrency control at the entity level:

```java
@Entity
public class CustomerJourney {
    @Version
    private Long version;  // JPA optimistic locking
    
    // Concurrent modification detection
    public void updateWithVersionCheck(Consumer<CustomerJourney> updater) {
        var currentVersion = this.version;
        updater.accept(this);
        
        // JPA will throw OptimisticLockException if version changed
        // during transaction, indicating concurrent modification
    }
}
```

**Version Conflict Resolution:**
When optimistic lock conflicts occur, the system provides resolution strategies:

**Automatic Retry:**
For simple operations, the system automatically retries with fresh entity state.

**User Notification:**
For complex operations, users are notified of conflicts and asked to review changes.

**Merge Strategies:**
When possible, the system attempts to merge non-conflicting changes automatically.

#### 9.4.2 Business-Level Concurrency Control

**2-Hour Exclusive Access Rule:**
Beyond technical concurrency control, the system implements business-level access rules:

**Access Control Implementation:**
```java
public class ConcurrencyControlService {
    
    public boolean canUserModifyJourney(CustomerJourneyId journeyId, UserId userId, ChannelType channel) {
        var journey = journeyRepository.findById(journeyId);
        
        // Same user can always modify
        if (journey.getLastModifiedBy().equals(userId)) {
            return true;
        }
        
        // 2-hour rule for different users
        if (journey.getLastModified().isBefore(LocalDateTime.now().minusHours(2))) {
            return true;
        }
        
        // Supervisor override capabilities
        if (hasOverridePermission(userId, journey.getLastModifiedBy())) {
            return true;
        }
        
        return false;
    }
}
```

**Concurrency Violation Handling:**
When business-level concurrency violations occur:

**User Notification:**
Clear messages explaining who has exclusive access and when it expires.

**Override Mechanisms:**
Supervisory staff can override access restrictions with proper justification.

**Audit Trail:**
All concurrency control decisions are logged for compliance and debugging.

#### 9.4.3 Deadlock Prevention

**Lock Ordering Strategy:**
To prevent deadlocks in multi-entity operations, the system establishes consistent lock ordering:

**Hierarchical Locking:**
- Customer entities locked before associated journeys
- Journey entities locked before module entities
- Module entities locked in consistent alphabetical order

**Timeout Management:**
Database and application-level timeouts prevent indefinite lock waits:

**Timeout Configuration:**
- Short timeouts for interactive operations (5-10 seconds)
- Longer timeouts for batch processing (30-60 seconds)
- Escalating timeouts for retry scenarios

**Deadlock Detection and Recovery:**
- Database-level deadlock detection and automatic victim selection
- Application-level retry logic for deadlock scenarios
- Monitoring and alerting for deadlock frequency patterns

### 9.5 Performance vs Consistency Trade-offs

#### 9.5.1 Consistency Level Optimization

**Graduated Consistency Model:**
The architecture implements different consistency levels based on business requirements:

**Strict Consistency (Immediate):**
- Financial transactions and payment processing
- Policy creation and cancellation operations
- Critical workflow state transitions
- **Performance Impact**: Higher latency, lower throughput
- **Business Benefit**: Zero data loss, immediate visibility

**Session Consistency:**
- User interface state and user-specific caches
- Shopping cart and preference management
- Draft document storage
- **Performance Impact**: Moderate latency, good throughput
- **Business Benefit**: Consistent user experience

**Eventual Consistency:**
- Reporting and analytics data
- Search indexes and lookup caches
- Non-critical system integrations
- **Performance Impact**: Low latency, high throughput
- **Business Benefit**: System scalability and availability

#### 9.5.2 Read vs Write Optimization

**Read-Heavy Optimization:**
Most system operations are read-heavy (browsing options, reviewing data), enabling specific optimizations:

**Read Optimization Strategies:**
- **Query Optimization**: Indexed queries for common access patterns
- **Projection Queries**: Load only required fields for display operations
- **Read Replicas**: Separate read-only database instances for reporting
- **Cache Layers**: Multiple levels of caching for frequently accessed data

**Write Optimization Strategies:**
- **Batch Processing**: Group multiple updates when possible
- **Async Processing**: Move non-critical writes outside transaction boundaries
- **Bulk Operations**: Use database bulk operations for large data sets
- **Write Coalescing**: Combine multiple updates to same entity

#### 9.5.3 Scalability vs Consistency Balance

**Horizontal Scaling Considerations:**
As the system scales, consistency guarantees may need adjustment:

**Current Architecture Constraints:**
- Single database instance limits horizontal scaling
- Strong consistency requirements prevent data partitioning
- Transaction boundaries span multiple business domains

**Future Scaling Patterns:**
- **Read Scaling**: Read replicas and caching layers
- **Functional Partitioning**: Separate databases per module
- **Eventual Consistency**: Relax consistency for non-critical operations
- **CQRS Implementation**: Separate read and write models

#### 9.5.4 Performance Monitoring Integration

**Transaction Performance Metrics:**
The system monitors transaction performance to identify consistency-related bottlenecks:

**Key Performance Indicators:**
- **Transaction Duration**: Average and 95th percentile completion times
- **Lock Contention**: Frequency and duration of lock waits
- **Rollback Rate**: Percentage of transactions requiring rollback
- **Concurrency Violations**: Frequency of optimistic lock failures

**Performance Alerting:**
- Transaction duration exceeding business requirements
- High rollback rates indicating design issues
- Lock contention suggesting concurrency problems
- Consistency violation patterns requiring attention

**Optimization Feedback Loop:**
Performance metrics inform architectural decisions:

**Consistency Relaxation Opportunities:**
- Operations with high consistency cost but low business risk
- Read-heavy operations that could use eventual consistency
- Cross-module operations that could be decoupled

**Scaling Decision Points:**
- When transaction volume approaches database limits
- When consistency requirements limit business agility
- When performance SLAs cannot be met with current consistency model

#### 9.5.5 Business SLA Alignment

**Consistency SLA Framework:**
The architecture aligns consistency guarantees with business service level agreements:

**Customer-Facing SLAs:**
- **Interactive Operations**: Sub-2-second response with strong consistency
- **Background Processing**: Eventual consistency within defined timeframes
- **Reporting Operations**: Near real-time consistency for operational reports

**Internal SLAs:**
- **Audit Trail**: Immediate consistency for compliance requirements
- **Financial Reporting**: Strong consistency for financial data
- **Operational Monitoring**: Eventual consistency acceptable for most metrics

**SLA-Driven Architecture Decisions:**
Business SLAs drive technical consistency choices:

**High-Priority Operations:**
Strict consistency requirements regardless of performance impact.

**Medium-Priority Operations:**
Balanced approach considering both consistency and performance.

**Low-Priority Operations:**
Performance-optimized with eventual consistency guarantees.

---

*This transaction management and data consistency architecture provides a robust foundation for handling complex business workflows while maintaining flexibility for future scaling requirements. The design balances strict consistency where needed with performance optimizations where business requirements allow, enabling both current operational efficiency and future architectural evolution.*

## 10. External System Integration

### 10.1 TIA Integration Architecture

**Integration Strategy:**
Direct Oracle database connectivity provides the primary integration mechanism with TIA, bypassing slower API layers and enabling transactional consistency. The architecture treats TIA as an authoritative data source while maintaining clean abstraction layers to isolate integration complexity.

**Connection Management:**
Dedicated connection pool with Oracle-specific optimizations handles the high-volume, low-latency requirements. Connection pooling configuration balances resource utilization with response time requirements, supporting concurrent user sessions during peak business hours.

**Data Ownership Model:**
TIA maintains authoritative customer records, policy data, and underwriting rules. The sales calculator acts as a sophisticated client that enriches TIA data with user experience optimizations while respecting TIA's role as the system of record.

**Integration Patterns:**
Stored procedure calls provide the primary integration mechanism, leveraging TIA's existing business logic while maintaining performance characteristics. This approach minimizes data transformation overhead and ensures business rule consistency across systems.

### 10.2 Adapter Pattern Implementation

**Abstraction Layer Design:**
Adapter pattern isolates TIA-specific integration details behind clean domain interfaces, enabling future migration scenarios and simplifying testing. Each module maintains its own TIA adapter focused on module-specific integration requirements.

**Transformation Responsibilities:**
Adapters handle bidirectional data transformation between internal domain models and TIA's database schema. This includes field mapping, data format conversion, and business rule translation while preserving semantic meaning.

**Error Translation:**
TIA-specific errors are translated into domain-meaningful exceptions that support business-appropriate error handling. This abstraction enables consistent error handling across modules while preserving diagnostic information for troubleshooting.

**Interface Standardization:**
All TIA adapters implement common patterns for connection management, error handling, and transaction participation, ensuring consistent behavior and simplifying maintenance procedures.

### 10.3 Circuit Breaker & Resilience Patterns

**Circuit Breaker Configuration:**
Module-specific circuit breakers protect against TIA system failures while maintaining user experience during degraded conditions. Failure thresholds and recovery timings are tuned based on business requirements and historical performance data.

**Graceful Degradation:**
When TIA is unavailable, the system provides cached data and limited functionality rather than complete failure. Users receive clear communication about reduced functionality with estimated recovery times.

**Retry Strategy:**
Exponential backoff with jitter prevents thundering herd scenarios during TIA recovery. Retry policies differentiate between transient errors (network timeouts) and permanent errors (business rule violations) to optimize recovery time.

**Fallback Mechanisms:**
Cached pricing data and previously calculated results enable continued operation during TIA outages. Business rules determine acceptable staleness for different data types, balancing user experience with data accuracy requirements.

### 10.4 Data Mapping & Transformation

**Bidirectional Mapping:**
Sophisticated mapping logic handles differences between user-friendly domain models and TIA's normalized database schema. Mappings preserve business semantics while optimizing for different usage patterns.

**Schema Evolution Support:**
Mapping layer accommodates TIA schema changes through versioned transformation logic. Multiple mapping versions can coexist during transition periods, enabling gradual migration without system downtime.

**Data Enrichment Patterns:**
Transformation layer enriches TIA data with calculated fields, derived values, and user experience optimizations. This includes risk score calculations, presentation formatting, and business rule application.

**Validation Integration:**
Data validation occurs at transformation boundaries to ensure consistency between systems. Validation rules reflect both domain requirements and TIA constraint requirements.

### 10.5 Integration Testing Strategy

**TIA Test Environment:**
Dedicated TIA test environment mirrors production schema and business rules while providing controlled test data. Test environment enables integration testing without affecting production operations.

**Contract Testing:**
Automated tests verify TIA integration contracts remain stable across releases. Contract tests focus on data format, business rule behavior, and error condition handling.

**Performance Testing:**
Load testing validates TIA integration performance under expected concurrent user loads. Performance tests identify bottlenecks and validate scaling assumptions.

**Failure Simulation:**
Chaos engineering practices test resilience patterns through controlled TIA failure simulation. These tests validate circuit breaker behavior, fallback mechanisms, and recovery procedures.

## 11. Cross-Cutting Concerns

### 11.1 Security Architecture & Implementation

**Authentication Strategy:**
Enterprise single sign-on integration provides seamless user authentication across systems. Authentication tokens include user identity, role information, and session context required for business operations.

**Authorization Model:**
Role-based access control with fine-grained permissions supports different user types (agents, call center staff, supervisors). Permission model aligns with business organizational structure and operational requirements.

**Data Protection:**
Encryption at rest and in transit protects personally identifiable information and financial data. Key management follows enterprise security standards with regular rotation and secure storage practices.

**Audit Requirements:**
Comprehensive security audit trail tracks all user actions and system access. Security logs integrate with enterprise SIEM systems for compliance monitoring and threat detection.

### 11.2 Logging, Monitoring & Observability

**Structured Logging:**
JSON-based structured logging enables efficient log aggregation and analysis. Log entries include correlation IDs, user context, and business process information for effective troubleshooting.

**Distributed Tracing:**
Request correlation across modules and external systems provides end-to-end visibility. Trace information enables performance analysis and bottleneck identification in complex workflows.

**Business Metrics:**
Application metrics focus on business outcomes rather than technical metrics. Key indicators include journey completion rates, conversion metrics, and customer experience measures.

**Real-Time Monitoring:**
Live dashboards provide operational visibility into system health and business performance. Monitoring integrates with enterprise observability platforms for centralized operations management.

### 11.3 Performance Monitoring & Optimization

**Response Time Tracking:**
Comprehensive response time monitoring across all user interactions and system operations. Performance data drives optimization decisions and capacity planning activities.

**Resource Utilization:**
Memory, CPU, and database connection monitoring prevents resource exhaustion scenarios. Resource utilization trends inform scaling decisions and infrastructure planning.

**Database Performance:**
Query performance monitoring identifies slow operations and optimization opportunities. Database metrics include connection pool utilization, query execution times, and lock contention analysis.

**Caching Strategy:**
Multi-level caching reduces database load and improves response times. Cache performance monitoring ensures optimal hit rates and identifies cache warming opportunities.

### 11.4 Error Handling & User Experience

**User-Friendly Error Messages:**
Technical errors are translated into actionable user messages that guide resolution. Error messages provide sufficient context without exposing sensitive system information.

**Progressive Enhancement:**
System provides basic functionality even when advanced features are unavailable. Graceful degradation ensures core business operations continue during partial system failures.

**Error Recovery Guidance:**
Users receive clear guidance on recovering from error conditions. Recovery procedures are designed to minimize data loss and reduce user frustration.

**Support Integration:**
Error handling integrates with customer support systems to enable efficient issue resolution. Support staff receive comprehensive error context and suggested resolution steps.

### 11.5 Configuration Management

**Environment-Specific Configuration:**
Configuration management supports multiple deployment environments with environment-specific settings. Configuration changes follow controlled deployment processes with proper testing and approval.

**Feature Toggles:**
Runtime feature toggles enable controlled rollout of new functionality and quick rollback capabilities. Feature flags support A/B testing and gradual user migration scenarios.

**External Configuration:**
Integration endpoints, business rules, and operational parameters are externally configurable. Configuration changes don't require application deployment for most operational adjustments.

**Configuration Validation:**
Automated validation ensures configuration consistency and prevents deployment of invalid configurations. Configuration validation includes business rule verification and integration endpoint testing.

## 12. Implementation Guidelines

### 12.1 Code Organization & Package Structure

**Domain-Driven Organization:**
Package structure reflects business domain boundaries rather than technical layers. This organization improves code discoverability and supports team ownership models.

**Dependency Direction:**
Clear dependency rules prevent circular dependencies and maintain architectural integrity. Dependencies flow inward toward domain core, with infrastructure depending on application and domain layers.

**Module Boundaries:**
Package-private visibility enforces module boundaries and prevents inappropriate coupling. Public APIs are explicitly defined through command and query interfaces.

**Shared Code Management:**
Common utilities and infrastructure code reside in shared packages with clear ownership and versioning. Shared code changes follow impact analysis and cross-team coordination processes.

### 12.2 Spring Boot Configuration Patterns

**Configuration Classes:**
Modular configuration classes organize Spring bean definitions by functional area. Configuration classes include comprehensive documentation and example configurations.

**Profile Management:**
Spring profiles support different deployment environments and testing scenarios. Profile configuration enables feature variations without code changes.

**Auto-Configuration:**
Custom auto-configuration simplifies application setup and reduces boilerplate configuration. Auto-configuration includes sensible defaults with clear override mechanisms.

**Testing Configuration:**
Specialized test configurations support different testing scenarios without affecting production configuration. Test configurations provide test doubles and simplified dependencies.

### 12.3 Testing Strategy (Unit, Integration, E2E)

**Test Pyramid Implementation:**
Comprehensive testing strategy follows test pyramid principles with emphasis on fast unit tests and targeted integration tests. Test distribution optimizes feedback speed while ensuring quality coverage.

**Unit Testing Standards:**
Unit tests focus on business logic verification with minimal external dependencies. Mock usage guidelines ensure tests remain maintainable and provide meaningful verification.

**Integration Testing Scope:**
Integration tests verify module interactions and external system integration points. Integration test environment provides realistic dependencies with controlled test data.

**End-to-End Scenarios:**
E2E tests validate complete business workflows from user perspective. E2E test suite covers critical business scenarios and edge cases that impact user experience.

### 12.4 Development Workflow & Team Practices

**Feature Development Process:**
Structured development workflow includes requirements analysis, design review, implementation, and testing phases. Development process supports parallel work while maintaining integration quality.

**Code Review Standards:**
Comprehensive code review process ensures code quality, architectural compliance, and knowledge sharing. Review criteria include business logic correctness, security considerations, and maintainability.

**Continuous Integration:**
Automated CI pipeline validates code changes through build, test, and quality analysis stages. CI process provides rapid feedback and prevents integration issues.

**Documentation Requirements:**
Code documentation standards ensure maintainability and knowledge transfer. Documentation includes business context, architectural decisions, and usage examples.

### 12.5 Code Quality & Standards

**Coding Standards:**
Consistent coding standards improve code readability and maintainability. Standards cover formatting, naming conventions, and structural patterns.

**Quality Metrics:**
Automated quality analysis tracks code coverage, complexity metrics, and architectural compliance. Quality gates prevent degradation of code quality over time.

**Refactoring Guidelines:**
Structured refactoring process ensures continuous code improvement without introducing defects. Refactoring guidelines balance improvement benefits with change risks.

**Technical Debt Management:**
Technical debt tracking and prioritization process ensures systematic improvement of code quality. Debt management balances new feature development with code quality maintenance.

## 13. Operational Concerns

### 13.1 Deployment Strategy & CI/CD

**Blue-Green Deployment:**
Zero-downtime deployment strategy enables frequent releases without business interruption. Blue-green deployment includes automated rollback procedures for rapid recovery from deployment issues.

**Database Migration Integration:**
Database schema changes integrate with application deployment process through automated migration scripts. Migration strategy supports both forward migration and rollback scenarios.

**Environment Promotion:**
Structured environment promotion process ensures consistent configuration across deployment stages. Promotion includes automated testing and validation at each stage.

**Release Automation:**
Fully automated release pipeline reduces human error and enables consistent deployments. Release automation includes smoke testing and health verification procedures.

### 13.2 Environment Configuration

**Environment Parity:**
Development, testing, and production environments maintain configuration parity to prevent environment-specific issues. Environment differences are minimized and well-documented.

**Infrastructure as Code:**
Environment provisioning uses infrastructure as code principles for consistency and repeatability. Infrastructure configuration supports rapid environment creation for testing and disaster recovery.

**Security Configuration:**
Environment-specific security configuration supports different threat models and compliance requirements. Security configuration includes network isolation, access controls, and monitoring integration.

**Scaling Configuration:**
Environment configuration supports horizontal and vertical scaling scenarios. Scaling configuration includes load balancer setup, database connection pooling, and cache configuration.

### 13.3 Database Migration & Versioning

**Schema Versioning:**
Database schema changes follow semantic versioning principles with backward compatibility guarantees. Schema versioning supports parallel deployment scenarios and rollback requirements.

**Migration Safety:**
Database migration procedures include safety checks and rollback procedures. Migration safety includes data backup, validation testing, and performance impact analysis.

**Data Transformation:**
Complex data migrations use staged transformation processes to minimize downtime and risk. Data transformation includes validation procedures and error recovery mechanisms.

**Production Migration:**
Production database migrations follow controlled procedures with comprehensive testing and approval processes. Production migration includes monitoring and rollback procedures.

### 13.4 Monitoring & Alerting Setup

**Proactive Monitoring:**
Comprehensive monitoring covers application health, business metrics, and infrastructure performance. Monitoring configuration enables proactive issue detection and resolution.

**Alert Configuration:**
Alert thresholds balance notification relevance with alert fatigue. Alert configuration includes escalation procedures and on-call rotation integration.

**Dashboard Design:**
Operational dashboards provide actionable information for different audience types. Dashboard design supports both real-time monitoring and historical analysis.

**Integration Platform:**
Monitoring integration with enterprise platforms enables centralized operations management. Integration includes alert forwarding, metric aggregation, and incident management.

### 13.5 Incident Response Procedures

**Incident Classification:**
Structured incident classification enables appropriate response procedures and resource allocation. Classification includes business impact assessment and resolution time expectations.

**Response Procedures:**
Documented incident response procedures ensure consistent and effective issue resolution. Response procedures include role assignments, communication protocols, and escalation criteria.

**Post-Incident Analysis:**
Comprehensive post-incident analysis identifies root causes and prevention opportunities. Analysis results drive system improvements and process refinements.

**Communication Management:**
Incident communication procedures keep stakeholders informed during issue resolution. Communication includes business impact updates and resolution progress reporting.

## 14. Evolution & Future Architecture

### 14.1 Scalability Planning & Bottleneck Analysis

**Performance Bottleneck Identification:**
Systematic analysis identifies current and projected performance bottlenecks. Analysis includes database performance, external system integration, and application processing bottlenecks.

**Scaling Strategies:**
Multiple scaling approaches address different bottleneck types and growth scenarios. Scaling strategies include horizontal scaling, vertical scaling, and architectural refactoring approaches.

**Capacity Planning:**
Data-driven capacity planning predicts resource requirements based on business growth projections. Planning includes infrastructure scaling, team growth, and technology refresh requirements.

**Technology Constraints:**
Current technology choices create scaling constraints that require architectural evolution. Constraint analysis drives technology migration and architecture modernization planning.

### 14.2 Cross-Product Reuse Strategy

**Abstraction Identification:**
Analysis of current architecture identifies reusable components for future insurance products. Abstraction candidates include workflow engines, data processing frameworks, and integration patterns.

**Library Extraction:**
Systematic library extraction process creates reusable components without disrupting current operations. Extraction process includes API design, testing, and documentation requirements.

**Platform Development:**
Evolution toward platform architecture enables efficient development of multiple insurance products. Platform development includes shared services, common frameworks, and standardized integration patterns.

**Governance Framework:**
Cross-product reuse requires governance framework for shared component evolution. Governance includes versioning policies, breaking change management, and support responsibilities.

### 14.3 TIA Migration & Exit Strategy

**Independence Planning:**
Systematic reduction of TIA dependencies enables future system migration scenarios. Independence planning identifies critical integration points and potential replacement strategies.

**Data Migration Strategy:**
Comprehensive data migration planning addresses the complexity of moving from TIA to alternative systems. Migration strategy includes data extraction, transformation, and validation procedures.

**Functionality Replacement:**
Analysis of TIA functionality guides replacement system selection and development priorities. Replacement analysis includes business rule migration and integration pattern evolution.

**Risk Mitigation:**
TIA migration carries significant business risk requiring comprehensive risk mitigation strategies. Risk mitigation includes parallel operation, rollback procedures, and business continuity planning.

### 14.4 Technology Evolution Path

**Framework Modernization:**
Spring Boot and Java platform evolution path ensures continued platform support and feature availability. Modernization includes migration planning, compatibility testing, and feature adoption strategies.

**Database Technology:**
Database technology evolution addresses scaling requirements and operational efficiency improvements. Database evolution includes cloud migration, technology refresh, and performance optimization opportunities.

**Integration Modernization:**
Integration technology evolution enables better performance, reliability, and maintainability. Modernization includes API-first design, event-driven architecture, and cloud-native integration patterns.

**Development Tooling:**
Development tooling evolution improves developer productivity and code quality. Tooling evolution includes build systems, testing frameworks, and development environment improvements.

### 14.5 Refactoring Roadmap

**Technical Debt Prioritization:**
Systematic technical debt analysis prioritizes refactoring efforts based on business impact and effort requirements. Prioritization includes maintenance cost analysis and business value assessment.

**Incremental Refactoring:**
Structured refactoring approach enables continuous improvement without disrupting business operations. Incremental approach includes risk assessment, testing strategies, and rollback procedures.

**Architecture Modernization:**
Long-term architecture modernization roadmap addresses scalability, maintainability, and technology currency requirements. Modernization includes microservices evolution, cloud migration, and platform architecture development.

**Team Capability Development:**
Refactoring roadmap includes team capability development to support architecture evolution. Capability development includes training programs, knowledge transfer, and expertise building initiatives.

## 15. Appendices

### 15.1 Architecture Decision Records (ADRs)

**Decision Documentation Framework:**
Structured ADR format captures architectural decisions with context, alternatives considered, and consequences. ADR framework enables decision traceability and supports future architectural evolution.

**Key Decision Records:**
Critical architectural decisions documented include monolith vs microservices choice, technology stack selection, integration patterns, and consistency models. Decision records provide rationale for current architecture and guidance for future changes.

**Decision Evolution Tracking:**
ADR evolution tracking documents decision changes and their impact on system architecture. Evolution tracking supports architectural governance and change impact analysis.

**Template Standards:**
Standardized ADR templates ensure consistent decision documentation across the project. Templates include sections for context, decision, rationale, alternatives, and consequences.

### 15.2 Technical Glossary

**Domain Terminology:**
Comprehensive glossary defines business domain terms used throughout the architecture documentation. Domain terminology ensures consistent understanding across technical and business stakeholders.

**Technical Terminology:**
Technical glossary defines architectural patterns, technology terms, and implementation concepts. Technical terminology supports knowledge transfer and onboarding processes.

**Acronym Dictionary:**
Complete acronym dictionary provides expansions and context for all abbreviated terms. Acronym dictionary improves documentation accessibility and reduces misunderstanding.

**Cross-Reference System:**
Glossary cross-references connect related terms and concepts throughout the documentation. Cross-reference system supports navigation and concept exploration.

### 15.3 API Reference & Examples

**Command Interface Documentation:**
Comprehensive documentation of all command interfaces including parameters, return values, and usage examples. Interface documentation supports development and integration activities.

**Event Schema Reference:**
Complete event schema documentation with examples and evolution guidance. Event documentation supports event handler development and system integration.

**Integration API Examples:**
Practical examples of TIA integration patterns and common usage scenarios. Integration examples accelerate development and reduce integration errors.

**Testing Examples:**
Code examples for testing different architectural components and integration patterns. Testing examples promote testing best practices and improve code quality.

### 15.4 Troubleshooting Guide

**Common Issues Resolution:**
Comprehensive troubleshooting guide addresses frequently encountered issues with step-by-step resolution procedures. Issue resolution includes root cause analysis and prevention strategies.

**Diagnostic Procedures:**
Systematic diagnostic procedures help identify and isolate system issues. Diagnostic procedures include log analysis, performance investigation, and integration troubleshooting.

**Recovery Procedures:**
Detailed recovery procedures for various failure scenarios ensure rapid system restoration. Recovery procedures include data recovery, state restoration, and service recovery steps.

**Escalation Guidelines:**
Clear escalation guidelines ensure appropriate expertise is engaged for complex issues. Escalation guidelines include contact information, severity criteria, and response time expectations.

### 15.5 Performance Benchmarks

**Baseline Performance Metrics:**
Comprehensive baseline performance measurements provide reference points for system optimization. Baseline metrics include response times, throughput, and resource utilization under various load conditions.

**Load Testing Results:**
Detailed load testing results document system behavior under expected and peak load conditions. Load testing results guide capacity planning and performance optimization efforts.

**Optimization Impact Analysis:**
Performance optimization impact analysis quantifies the benefits of architectural and implementation improvements. Impact analysis supports optimization prioritization and investment decisions.

**Benchmark Methodology:**
Standardized benchmark methodology ensures consistent and repeatable performance measurement. Methodology documentation enables performance comparison across system versions and configuration changes.

---

*This comprehensive architecture documentation provides the foundation for developing, operating, and evolving the insurance sales platform. The documentation balances current implementation guidance with future evolution planning, supporting both immediate development needs and long-term architectural success.*