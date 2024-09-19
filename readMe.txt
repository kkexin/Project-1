Project Overview
This application (SymptomActivity) is a symptom aware application used to record and track the health status of users. Users can record their current health status by selecting pre-defined symptoms and rating them. The data will be stored in a SQLite local database for future reference.

Question and Answer
1. Imagine you are new to the programming world and not proficient enough in coding.  But, you have a brilliant idea where you want to develop a context-sensing application like Project 1. You come across the Heath-Dev
paper and want it to build your application. Specify what Specifications you should provide to the Health-Dev framework to develop the code ideally.
answer:
If we want to develop a context aware application centered on health like this, the specifications provided to the Health Dev framework should include the following points:
Sensor data requirements: Applications need to use mobile phone sensors (such as accelerometers, cameras) to collect health-related data, such as heart rate, respiratory rate, etc.
Symptom selection and scoring mechanism: The application should allow users to select corresponding symptoms from a predefined symptom list and score each symptom through a slider. The symptom list and rating will reflect the user's current health condition.
Data storage and processing requirements: Symptoms and scores need to be recorded in a local database (such as SQLite) to ensure that the data can be saved for long-term analysis.
Data analysis and feedback mechanism: The framework should support the analysis of symptom data and provide feedback on user health status. Personalized suggestions can be proposed based on the trend of rating calculation.
Interface requirements: The framework should allow for the design of a simple and user-friendly interface, allowing users to easily select symptoms and view entered data.
Data security and privacy requirements: Applications must ensure the security of users' health data, and frameworks should support data encryption and secure database storage mechanisms.
2. In Project 1 you have stored the user’s symptoms data in the local server.  Using the bHealthy application suite how can you provide feedback to the user and
develop a novel application to improve context sensing and use that to generate the model of the user?
answer:
Through the bHealthy application suite, users' symptom data can be analyzed in depth and real-time feedback can be provided. The specific steps are as follows:
Data aggregation and trend analysis: Aggregate users' data from multiple days and analyze their symptom trends. BHealthy can use machine learning or statistical methods to identify potential health problems or diseases that may worsen.
Personalized health feedback: Based on symptom data and ratings, the application can provide users with personalized feedback. For example, suggestions for users to rest, increase water intake, or seek medical attention. Feedback should not only be based on current symptoms, but also refer to historical data to provide more accurate recommendations.
Improvement of context awareness: By adding additional sensors or information sources such as GPS positioning, activity tracking, etc., the context awareness function can be further enriched. For example, providing more targeted health recommendations based on users' exercise patterns and environmental changes.
Generate user model: Based on historical data, the application can generate a user's health model and track their long-term health status. This model can be used to predict future health changes and remind users to take action before symptoms worsen.
3. A common assumption is mobile computing is mostly about app development. After completing Project 1 and reading both papers, have your views changed? If
yes, what do you think mobile computing is about and why? If no, please explain why you still think mobile computing is mostly about app development, providing
examples to support your viewpoint
answer:
Changed perspective: After reading relevant papers and completing the project, my understanding is that mobile computing is far more than just application development. It covers a wide range of technical fields and research directions, such as:
Sensor integration and data processing: Mobile computing not only involves developing applications, but also includes how to use various sensors in mobile phones to collect data, and process and analyze this data in real-time. For example, in this project, sensors such as accelerometers and cameras are used to monitor the health status of users.
Data analysis and model generation: Mobile computing also focuses on how to analyze sensor data and generate personalized user models. This is not limited to front-end application development, but involves the application of complex calculations and machine learning models in the back-end.
Context aware: Mobile devices are not just tools for running applications, they are also part of the user environment and can understand the user's context through sensors. Through context aware technology, applications can provide more personalized services based on the user's status or location.