FLS Service Desk Android
Open this folder in Android Studio and use Build > Build APK(s). The app asks for the FLS server URL on first launch. For your current local development server, connect the phone and computer to the same Wi-Fi and enter http://192.168.1.33:8788. For public use, deploy the server with HTTPS and use that URL.
The app uses the existing authenticated service workflow, can copy/share assistant links, and saves Excel exports through Android's file picker. Online connection is required; requests are stored on the FLS server.

Android update 1.0.1
- Fixes Android edge-to-edge status bar overlap and replaces a failed blank WebView with a connection message, retry button, and server-address settings.
- Reinstall this APK over version 1.0.0 to update. The same debug signing key is used, so the saved server address should remain on the phone.
- A local development server is reachable only from devices that can access that computer over the network. If the phone cannot reach it, use the hosted HTTPS server address in the app's Server settings.
- Build checks completed: Java compilation, APK package/version inspection, APK signature verification, and ZIP alignment verification. No phone was attached and the emulator could not start because its AVD files are outside the writable workspace.

Responsive update 1.0.3
- Responsive web content refinements for 320 dp phones, small phones, portrait tablets, and landscape tablets; better touch sizes, horizontal scrolling for wide tables, and keyboard-safe forms/dialogs.
- Native setup/error panels now use centered cards capped at 560 dp and safe-area insets, with scrolling on short screens. Existing installations can update in place using the same debug signing key.
- Visual previews were captured at 393x852, 800x1280, and 1280x800 browser viewports. APK Java compilation, package metadata, signature, and alignment checks passed. Android emulator boot was unavailable because the AVD needs more free disk space than this machine has.

Assigned vehicles and notifications
- Supervisor overview and vehicle tables now show the assigned service assistant. The Excel export also includes an Assigned assistant column.
- After sign-in, service assistants see a My assigned vehicles list with vehicle number, ticket, customer, issue, current status, and Open job action. The list refreshes every 12 seconds from an authenticated, assistant-filtered endpoint.
- Enable alerts requests native Android notification permission in the APK. Browser notifications are available on HTTPS when the user grants permission. Assignment alerts are checked while the app/page is open; background push delivery needs a push provider such as Firebase Cloud Messaging and server credentials.

Assignment and hourly progress update 1.0.5
- Supervisor service request/vehicle rows show assistant assignment time and, after supervisor marks a ticket Completed, completion time. Excel includes both timestamps.
- Assistants see assignment time, last progress note/time, and can submit hourly progress and one of In progress, Waiting for parts, or Ready for delivery. Progress becomes visible to the supervisor and in the activity log.
- The app gives an hourly progress reminder while the assistant is signed in with the app open and notifications enabled. Android may pause delivery while the app is backgrounded; dependable background push needs Firebase Cloud Messaging.


Android blank-screen repair 1.0.5
- Updates saved local server address http://192.168.1.33:8788 to http://192.168.1.33:8789 automatically on launch, preserving installation settings and sign-in/server-side data.
- Connection failures stay on a readable retry/server settings screen instead of showing a blank WebView.
- Install over the prior APK; package name and debug signing key are unchanged. This build was compiled, aligned, signed, and package metadata verified. No phone/emulator was attached for a live install check.


Connection fix 1.0.6
- Returns the app to port 8788, which is already allowed for local Wi-Fi access on this computer. Upgrades from 1.0.5 automatically migrate the saved server address from port 8789 back to 8788.


Current connection address for this workspace: http://192.168.1.33:8788. Version 1.0.6 supersedes the temporary port-8789 change in 1.0.5 and migrates a saved 1.0.5 address back to port 8788.


Direct login launch 1.0.7
- The startup server-address screen and header Server button are removed. Fresh installs connect to the configured workspace address and open FLS login directly; address editing stays available on the connection error screen.


Connection reliability update 1.0.8
- Migrates legacy saved ports 8787 and 8789 to the current local service at 8788.
- The Android WebView reveals page content as soon as it is committed instead of waiting for optional external scripts. SheetJS loading is asynchronous and cannot hold up the login screen.


HTTP scheme repair 1.0.9
- If the saved local server URL uses https://192.168.1.33:8788, it is normalized to the local HTTP server address http://192.168.1.33:8788 before loading. This prevents ERR_SSL_PROTOCOL_ERROR.


Assistant logout and vehicle transfer
- Service assistants can sign out from both their assigned-vehicle page and service report page.
- Assistants can transfer active assigned vehicles to another assistant account. The new assignee receives the vehicle, assigned timestamp resets, and the transfer is recorded in activity for the supervisor and Excel export. Completed/service-report-submitted work cannot be transferred.
- The Android APK loads the web workflow from the server, so this web/backend update appears after refreshing the installed app; no native APK rebuild is needed for these controls.


Fuel Sensor Care logo 1.1.0
- Supplied logo is used in the Android toolbar and launcher icon, web sidebar, assistant views, login card, and PWA icon.
- The app/web brand images are server-served; Android toolbar and launcher changes are in APK v1.1.0.

