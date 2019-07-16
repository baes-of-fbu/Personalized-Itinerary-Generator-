# Personalized-Itinerary-Generator

# Personalized Itinerary 

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
App gives you an itinerary of a day in Seattle based off your budget.

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Travel
- **Mobile:** Accesses location and is connected to other apps such as Maps. Has to be refrenced throughout the entire day, you need it on your phone.
- **Story:** Optimizing time in a new city and takes away the stress of filtering through several websites while trying to stay in budget.
- **Market:** Anyone new to Seatle. Really good for short-term trips but applicable many. Great for all ages.
- **Habit:** Come back because of the savings and efficiency of the app.
- **Scope:** Starting off with Seatle but eventually expanding to other cities around the world. 

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**


* Use Parse to make a backend and login features and profiles: include name, picture, original city, email, phone number
* Create a form that takes in the users preferences, travel times, and budget (multiple choice) as inputs. Make sure that all inputs are valid
* Output a schedule that is in budget

**Optional Nice-to-have Stories**

* User can modify schedule
* Share schedule with other users
* Multiple travelers
* Save as Draft
* Suprise schedule
* Stickers
* Connecting to Lyft or Uber
* Explore page for evens (Pintrest-like layout with tags to filter events)
* export to google cal 

**Buttons to add but wont implement** 
* Report User

### 2. Screen Archetypes

* Login
   * Taking you to a sign up page if you don't have an account
   * If you already have an account you would be taken to a home page showing existing itinerary
* Sign up 
   * ALlows user to create a profile
* Timeline
    * Shows old trips and can send you to new trip, profile, and log out
* Preferences
    * Enter in you prefences and submit then taking you to the intermediate screen
* Intermediate
    * Accepts or Deny itinerary
    * takes you back to the timeline


### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Home
* Profile
* New Trip

**Flow Navigation** (Screen to Screen)

* Login
   * Sign up
   * Home
* New Trip
   * Generare => Home

## Wireframes
[Add picture of your hand sketched wireframes in this section]
<img src="YOUR_WIREFRAME_IMAGE_URL" width=600>

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 

### Models
#### User
|    Property      | Type        | Description |
   | ------------- | ----------- | ----------- |
   | objectId      | String      | unique id for the user post (default field) |
   | username      | String      | name of user |
   | password      | String      | passworod for user |
   | email         | String      | email for user |
   | homeState     | String      | Two-letter abbreviation of home state |
   | profileImage  | File        | image for user's profile |
   | Trips         | List<Trip>  | List of trips that user created |
   | Age (stretch) | Array<Int>  | Age of user, updated to today's date |
   | createdAt     | DateTime    | date when user was created |
   | favEvents     | List<Event> | list of favorite events |

#### City
|    Property      | Type               | Description      |
   | ------------- | ------------------ | ---------------- |
   | cityEvents    | List<Events>       | List of all the events in a city
   | categorizedEvents | Map<String, List<Events>> | Each key is a category and the corresponding value is a list of events associated with that category |
   | cityName          | String             | Name of the city  |
   | cityState         | String             | Name of the state |
   | cityLocation      | LatLong(?)         | Location of the city |
   | cityDescription   | String             | Description of the city |
   | cityCoverPhoto    | File               | Image for the city |
   | cityAllCategories | List<String>       | List of all the categories in a given city |

#### Event
|    Property      | Type    | Description      |
   | ------------- | --------| -----------------|
   | objectId      | String  | Unique id for the event |
   | *eventName     | String  | Name of the event |
   | *eventDescription | String | Description of event |
   | *eventImage    | File    | Image of the event |
   | *eventPrice    | Double   | Cost of the event | 
   | isFavorite    | boolean | True if user favorited event, false otherwise |
   | eventCategories | List<String> | Categories to describe the event |
   | *eventLocation | Address(?) | Stores the address of the event |
   | *Hours         | Time(?)   | Stores the hours of operation for the event |
   | Length         | Double    | Length of the event, hours.minutes (ex: 1.5 = 1 hour and 30 minutes) | 

#### DayPlan
|    Property      | Type        | Description      |
   | ------------- | ----------- | -----------------|
   | objectId      | String      | Unique id for the event |
   | Name          | String      | Name of the day |
   | Start Time    | Time(?)     | Starte time for the day |
   | End Time      | Time(?)     | End time for the day |
   | Budget        | Double      | Amount of money to spend |
   | Events        | List<Event> | List of events in the day |
   | Date          | DateTime    | Date of the day |
   

#### Trip
|    Property      | Type            | Description      |
   | ------------- | --------------- | -----------------|
   | Day           | List<DayPlan>    | A list DayPlans in the trip |   
   | Name          | String          | Name of Trip     |
   | owner         | Pointer to User | author of the trip |
   | startDate     | DateTime        | First day of the trip |
   | endDate       | DateTime        | Last day of the trip |
   | Budget        | Double          | Max amount of money to spend | 
   | isUpcoming    | boolean         | True if event has not happened, false if already occured |
   | isPosted      | boolean         | True if user wants to publish to timeline, false otherwise |

### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
