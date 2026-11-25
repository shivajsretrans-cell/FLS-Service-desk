FLS Service Desk — hosted login build

Local preview
- Install Node.js 22 or newer.
- Copy .env.example to .env and set BOOTSTRAP_TOKEN to a long random value.
- Run node server.js, then open http://localhost:8787.
- The first Owner setup asks for the one-time bootstrap token. If you do not set one, the server prints a generated token to its startup log.

Accounts and workflow
- First-time setup creates the Owner account. The existing Shiva Sharma account has been promoted to Owner. Owner uses Team & access to create Supervisor or Service Assistant accounts (name, email, role, password). Owner can turn supervisor vehicle assignment on or off; Owner always retains assignment access. Service Assistant access remains limited to assigned vehicles.
- Create a request, select the assigned assistant's account, then use Share link.
- The assistant signs in using their account, fills Problem Found and Action Taken, confirms OK, and submits. The report stores the signed-in assistant name and updates the ticket to Service report submitted. The supervisor dashboard refreshes automatically.
- Service assistants can only open a linked ticket report. Manager APIs protect service request visibility; user creation and access controls are Owner-only.
- Passwords use Node scrypt hashes; sessions use HttpOnly SameSite cookies with Secure enabled in production. Login attempts are rate-limited.

Owner controls and recovery
- Owner can change a user role between Supervisor and Service Assistant, activate or deactivate accounts, and change passwords. Role, activation, and password changes end the target account sessions; changing your own Owner password signs you out so you can sign in again.
- Assistant progress requests stream to the signed-in assistant over Server-Sent Events, with the existing periodic refresh as a fallback. Request and reply timestamps, reply delay, status, message, and activity history appear in the dashboard and Excel export. Background push when the Android app is fully closed still depends on an external push provider.
- Server snapshots run after saved changes (coalesced) and every six hours. Owner can create, download, or restore a snapshot, including an uploaded JSON copy. Backups contain service records and password hashes and should be handled as sensitive files. The data directory must be on persistent storage for backups to survive host replacement.

Hosting
- render.yaml is a Render Blueprint with a paid 0.5 CPU / 512 MB web service and 1 GB persistent disk mounted at /var/data. The persistent disk keeps tickets and user accounts across restarts and deploys. Render documents that persistent disks require a paid service and limit the attached service to one instance; see https://render.com/docs/disks and https://render.com/docs/blueprint-spec.
- To deploy, place this outputs folder in a Git repository, connect that repo to Render, and create a Blueprint from render.yaml. Render generates BOOTSTRAP_TOKEN for the Blueprint; retrieve it from the service environment or startup logs for first-time supervisor setup, and keep it private.
- Dockerfile is included for other container hosts. Set a persistent volume at /data and use HTTPS in front of the app.

Important limits
- This is a small single-instance prototype using JSON files, not a multi-instance production database. It has no password-reset flow, MFA, audit retention controls, or automated backups. For wider production use, replace the file store with managed PostgreSQL and add backups and account recovery.
- FLS-Service-Activity.xlsx is sample data. The in-app Excel button exports current live requests and activity.

Android app
- This app can be installed on Android from Chrome after the service is deployed on an HTTPS address (the Render Blueprint provides HTTPS).
- Open the HTTPS app address in Chrome on the phone, sign in, then choose Chrome menu > Install app / Add to Home screen. It opens full-screen as “FLS Service” and uses the same secure accounts, service requests, assistant reports, follow-ups and Excel export.
- Keep the phone online while using it. Tickets and reports are stored by the server; the Android app does not keep a separate offline copy.
- Android Studio / Android SDK are not installed in the current development workspace, so no signed APK is produced here. This installable Android web app uses the existing backend and does not require a second app account.

Native Android project
- Android Studio source is in android-app/. Open that folder in Android Studio, sync Gradle, and choose Build > Build Bundle(s) / APK(s) > Build APK(s). See android-app/README.txt for the server URL setup and build requirements.

Android APK
- Installable debug APK: FLS-Service-Android-debug.apk. Copy it to an Android phone and open it to install. At first launch, enter the FLS server address; for the local server use http://192.168.1.33:8788 while the phone and computer are on the same Wi-Fi.
- Native Android project source: android-app/. This APK was built with the installed Android SDK tools and signature/ZIP alignment verification passed. A connected phone was not available for installation testing.
- Android update 1.0.1 adds a safe top inset for the phone status bar and displays a connection error with retry/server settings instead of an empty page. APK signature and alignment checks passed; see android-app/README.txt.
- Android v1.0.3: responsive phone/tablet web layouts plus centered, safe-area-aware native setup/error screens. See android-app/README.txt.
- Assistant assignments now appear in supervisor tables and in each assistant's My assigned vehicles list; assignment alerts can be enabled in the assistant view.

Hourly progress and service timestamps
- Supervisor dashboard and Excel now show when a vehicle was assigned and when it was marked Completed. Completion time is recorded when the supervisor changes status to Completed.
- Assistant progress notes and status changes are saved to the activity log and visible on the supervisor page.
- Assistants receive hourly reminders while signed in with the app open and notifications enabled. Android and browser operating systems can suspend reminders while the app is closed or in the background; background push needs a configured push service.


Android blank-screen repair 1.0.5
- The replacement APK auto-migrates the saved local FLS server from port 8788 to port 8789 and shows a connection/retry panel if the server cannot be reached.
- Use the computer and phone on the same Wi-Fi. If the phone still cannot reach the updated service, Windows Firewall must allow inbound TCP 8789 on the local subnet; this session could not create that rule because Windows returned Access denied.


Current mobile app server address: http://192.168.1.33:8788/ (phone and computer must be on the same Wi-Fi).


Android 1.0.7 direct login
- Fresh installs open the FLS login screen directly using http://192.168.1.33:8788; no startup server-address form is shown. Existing 1.0.5/1.0.6 installs keep their saved server, and the former 8789 setting migrates to 8788.
- The server address form remains available only from the connection error screen.


Assistant logout and vehicle transfers
- Assistant pages now provide Sign out and active vehicle transfer to another assistant. Transfers update the assignee/time, move the job into the new assistant’s list, and add an auditable supervisor activity/Excel row.
- Refresh the Android app at the current server URL to load the updated workflow; its WebView app does not require a new APK for these server-served controls.


Vehicle contact and completed report details
- Assistant vehicle cards show the customer contact number and, after a report is submitted, the problem found and action taken. Supervisor vehicle tables also show contact/report details, and Excel includes Problem found and Action taken fields.
- The Android app displays the same server-served web screen. Reopen/refresh it to load this change; no APK reinstall is needed.


Branding update 1.1.0: user-provided Fuel Sensor Care logo is applied to app/web UI and icons.

