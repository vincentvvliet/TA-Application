## Week 8

Gijs:
- Issue 22: finish all testing: Testing was yet another large part of this issue, I already had some tests after week 7, but after Andrei introduced me to the MockBackEnd testing got a lot clearer for me for the service methods that make a request to a different microservice.  I walked into the issue of having a service method that was untestible, since it called helper method in the same file which needed to be mocked to be tested. To solve this I seperated the ApplicationService into ApplicationService, RecommendationService and CommunicationService.

	Estimated time spent: see [Week 6](./SprintRetrospective6.md).
	Actual time spent: 8 hours.
	
---
Ravi:
- I still had the same issue as previous week in that the work I still needed to do relied on the work of others. And their work was not yet merged at this point in time.