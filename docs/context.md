# Insurance Sales Platform - Essential Architecture Knowledge (Revised)

## System Overview
**Hexagon-with-BFF** - Motor insurance sales platform rewriting legacy calculator for agents and call center staff. Enables 7-day resumable quotation workflows across multiple channels with goal of creating domain-driven solution attractive to tariff management team.

## Business Context
- **Legacy Replacement**: Modernizing existing motor insurance calculator with new tech stack
- **Cross-Product Strategy**: Architecture designed for reuse across future insurance products (home, commercial, life)
- **Sales Workflow**: Agent desktop → Call center → Online portal progression with different rules per channel
- **Insurance Domain**: Coverage options, deductibles, underwriting rules, rate filings, product versioning
- **Price Guarantees**: 30-day offering validity with product version locking for regulatory compliance

## Core Architecture
- **Pattern**: Modular monolith with command-driven architecture and event-driven coordination
- **Runtime**: Single Spring Boot application with package-based domain modules
- **Technology**: Java 21, Spring Boot 3.2.5, Oracle Database, Gradle multi-module
- **Team Structure**: 4 developers - 1 per domain module + tech lead managing CustomerJourney orchestration

## Domain Modules & Ownership
- **CustomerJourney**: Workflow orchestrator (Tech Lead) - state transitions, cross-module coordination
- **DataProcurement**: Customer data collection (Dev 1) - validation, enrichment, TIA data integration
- **Offering**: Pricing calculation (Dev 2) - TIA/Earnix integration, discounts, product rules
- **Checkout**: Payment processing (Dev 3) - policy creation, compliance, idempotency guards
- **Shared**: Common infrastructure - TIA integration layer, utilities, security

## Key Architectural Decisions
- **Monolith over Microservices**: Team size (4 developers), shared Oracle database, deployment simplicity
- **Command interfaces**: Prevent direct module dependencies, enable future extraction, clear testing boundaries
- **Synchronous events with journey ID correlation**: ACID consistency, pragmatic debugging over mapping tables
- **Direct Oracle integration**: TIA API limitations, performance requirements, transaction control
- **Package boundaries with ArchUnit**: Module separation without deployment complexity

## Integration Complexity
- **TIA System**: Oracle-based policy management containing core business logic in PL/SQL procedures
- **TIA Ownership**: Authoritative customer database, underwriting rules, policy templates, business validations
- **Earnix Access**: Sophisticated pricing engine accessible only through TIA proxy calls (no direct integration)
- **Data Transformation**: Complex mapping between user-friendly domain models and TIA normalized schema
- **Integration Patterns**: Stored procedure calls, adapter pattern isolation, circuit breaker resilience

## State Management & Workflow
- **Aggregate Root**: CustomerJourney contains minimal state references (ID + status) to rich module entities
- **State Machine**: Explicit transitions with cascade reset logic (data changes reset downstream offerings/checkout)
- **Concurrency Control**: 2-hour exclusive access rule between users/channels + JPA optimistic locking
- **State Value Objects**: Immutable embedded objects for workflow coordination without exposing module complexity

## Long-Running Process Specifics
- **7-Day Workflow**: Customer can pause/resume across channels with persistent intermediate states
- **Cross-Channel Resume**: Agent → Call center → Online with different business rules per channel
- **State Expiration**: Automatic cleanup policies, offering validity periods, process timeout handling
- **Manual Intervention**: Complex cases requiring human review, supervisor overrides, manual completion

## Command Pattern Implementation
- **Command Interfaces**: Clean module boundaries preventing horizontal dependencies
- **Processing Pipeline**: Security → Concurrency → Business validation → Execution → Audit
- **Validation Framework**: Multiple severity levels (errors block progression, warnings allow with manual review)
- **Error Classification**: Business rule violations, external system failures, transient errors with appropriate recovery

## Event Architecture
- **Domain Events**: State changes trigger workflow coordination, module reactions, audit trail
- **Journey ID Correlation**: Every event includes CustomerJourneyId for direct lookup and debugging
- **Synchronous Processing**: Within same transaction for consistency, ordered execution with @Order priorities
- **Idempotency**: Event handlers safe for replay, duplicate detection, compensating actions

## Transaction Strategy
- **Single Transaction Boundary**: Command execution + state updates + immediate event processing
- **Consistency Levels**: Immediate for workflow/financial data, eventual for caching/reporting
- **Rollback Patterns**: Automatic for failures, compensating transactions for external system issues
- **Performance vs Consistency**: Strong consistency for critical operations, relaxed for non-critical

## Critical Requirements
- **Resumable Workflows**: 7-day processes with state persistence across channels
- **Omnichannel Support**: Agent desktop, call center, future online portal with channel-specific rules
- **Concurrency Management**: 2-hour exclusive access with same-user exceptions and supervisor overrides
- **Performance**: Sub-2-second response, 50+ concurrent users, 8AM-8PM peak usage
- **Compliance**: Complete audit trail, PII encryption, regulatory rate filing compliance
- **Product Versioning**: Price guarantee integrity during long sales processes

## External System Dependencies
- **TIA Constraint**: Oracle database housing core business logic, customer records, policy management
- **Earnix Limitation**: No direct access, must use TIA proxy for sophisticated pricing calculations
- **Performance Bottlenecks**: External system latency impacts user experience, requires caching strategies
- **Resilience Patterns**: Circuit breakers, retry logic, graceful degradation, fallback mechanisms

## Evolution & Scaling Path
- **Module Extraction**: Command interfaces can become REST APIs when team/requirements scale
- **TIA Exit Strategy**: Gradual dependency reduction through adapter isolation, business logic extraction
- **Cross-Product Reuse**: Library extraction for data transformation, workflow engine, validation framework
- **Technology Evolution**: Spring platform updates, potential cloud migration, read replica scaling

## Testing & Quality Strategy
- **Unit Tests**: Business logic isolation with minimal dependencies
- **Integration Tests**: Module interactions, TIA integration contracts, workflow scenarios
- **E2E Tests**: Critical business workflows, cross-channel scenarios, error recovery
- **Architecture Tests**: ArchUnit enforcement of module boundaries and dependency rules

## Performance & Monitoring
- **Caching Strategy**: Multi-level caching for TIA responses, customer data, pricing calculations
- **Database Optimization**: Oracle-specific connection pooling, query optimization, index strategies
- **Business Metrics**: Journey completion rates, conversion metrics, customer experience measures
- **Operational Monitoring**: Real-time dashboards, proactive alerting, comprehensive audit trails

## Operational Patterns
- **Deployment**: Blue-green deployment with database migration integration
- **Error Recovery**: Dead letter processing, manual intervention workflows, state reconstruction
- **Incident Response**: Classification procedures, escalation paths, post-incident analysis
- **Configuration Management**: Environment-specific settings, feature toggles, business rule externalization