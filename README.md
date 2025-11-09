# MEC-EchoTeam: Emergency AI Assistant  
> “When disaster strikes, we don’t panic — we deploy.”  


## Tech Stack

| Layer | Technologies |
|:------|:-------------|
| **Frontend** | React • JavaScript • CSS |
| **Backend** | Java • Spring Boot 3.5.7 • DeepSeek API • H2 Database |
| **Runtime** | JDK 17 • Node.js 20.19.4 • npm 10.8.2 |


## Current Stage

Right now, only **DeepSeek API** is enabled (because OpenAI tokens are priced like caviar 🐟).  

---

## Setup Guide

### 1. Backend Setup (Spring Boot)

#### Requirements
- Java **JDK 17+**
- Maven (optional if using IDE run config)
- Spring Boot version **3.5.7**

#### Run Command ( Backend) 
./mvnw spring-boot:run  
( It meight be slightly different between Mac and Windows, But I know you will fix it !) 

### Run Command ( FrontEnd) 
-npm install  
-npm start


 --- 
 If you just want to test the backend with Postman, here is the example  

 POST http://localhost:8080/api/analyze
 
 {
  "transcript": "Caller says there is a fire in the basement, people might be trapped.",
  "phoneNumber": "+12025551234",
  "timeReported": "2025-11-09T14:00:00Z"
}

{
  "transcript": "Hello, this is Sarah calling from downtown Toronto. There has been a serious multi-vehicle collision near the intersection of King Street and Spadina Avenue. One car is on fire, another vehicle flipped over, and there are at least three people injured. One man seems unconscious and not breathing, and two others have visible bleeding wounds. The traffic is completely blocked, and smoke is spreading fast. Please send emergency responders immediately.",
  "phoneNumber": "+16475551234",
  "timeReported": "2025-11-09T14:32:00Z"
}






