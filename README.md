Project Name: Event Management App

Group Members:
- M Hussnain Jamil

Contribution:
- 100% of the work was done by ME

Percentage of Completion:
- 100%
  

Typical User Flows:

Admin:
- Log in with pre-defined admin credentials
- View list of all events
- Create, edit, or delete events
- View list of students who joined each event(In separate Activity)
- All changes are immediately reflected in the event list

Student:
- Log in with student credentials or register a new account
- Browse the list of events
- Tap an event to see details


---

Feature Breakdown & Completion:

1. Login & Registration
- Student/Admin login via MainActivity: 100%
- Registration with email, username, password: 100%
- Persistent login using SharedPreferences: 100%

2. UI Feedback & Interactions
- Join button disables and turns gray after joining: 100%
- Visual feedback on button press using custom XML drawable: 100%
- Clickable "Register" text with styled appearance: 100%

3. Event Management
- Student event list via RecyclerView: 100%
- Admin event list with edit/delete options: 100%
- Event details screen with full info and image: 100%
- Edit screen shows previously saved data: 100%
- Join event feature for students with button disabled after join: 100%
- Admin can view list of students who joined each event: 100%


4. Image Handling
- Glide used to load event images with placeholders and error fallback: 100%

5. Database & Data Handling
- SQLite database implementation for users, events, and joined events: 100%
- Full CRUD functionality: 100%

6. UI/UX & Layout
- Material Design components and consistent theme: 100%
- Responsive layouts using ConstraintLayout & LinearLayout: 100%
- Toast messages for all error cases and invalid input: 100%

7. Code Structure & Organization
- Proper separation into Activities, Adapters, and DB Helpers: 100%
- Resources (drawables, layouts, values) properly structured: 100%

---

Final Notes:
- Complete error handling implemented
- Editing an event loads previous values for smooth updates
- Fully tested for admin and student flows

---

🧪 For Testing:

**Admin Credentials** (pre-defined, cannot be created):
- Email: `admin@test.com`
- Password: `admin123`
