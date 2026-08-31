# Site Visit — Android App

A native Kotlin / Jetpack Compose app for field-service work: log site locations
on a map, schedule visits with reminders, track payments/balances per site,
create quotations, and take/save site photos.

## What's included

- **Sites** — add a site with name, address, contact info, and GPS coordinates
  (captured live from the device, or entered manually).
- **Map** — every site with a location shows as a pin on an OpenStreetMap view
  (via osmdroid — no API key needed, unlike Google Maps SDK).
- **Schedule** — create visits tied to a site, with date/time and an optional
  reminder (15 min / 30 min / 1 hr / 1 day before). Reminders fire as system
  notifications via `AlarmManager`, and are automatically rescheduled after a
  device reboot.
- **Quotations** — build a quotation with line items (description, qty, unit
  price), a discount, and a tax %; totals calculate automatically.
- **Payments & balances** — log charges and payments per site; each site shows
  a running balance owed.
- **Photos** — take a photo with the device camera and attach it to a site;
  photos are stored in the app's private external storage and shown in a
  gallery grid.

All data is stored locally on-device with a Room (SQLite) database — nothing
leaves the phone, no backend required.

## Building this from your Android phone (no computer needed)

Android Studio doesn't run on Android, so you can't compile this project
directly on your phone the normal way. The practical workaround: push the
code to GitHub and let **GitHub Actions** (free for public/small private
repos) compile the APK in the cloud — you just download the finished file.

**What you need on your phone:** the [Termux](https://termux.dev) app (install
from F-Droid, not the outdated Play Store version) and a free GitHub account.

### Steps

1. **Install Termux** from F-Droid.
2. **Get this project onto your phone.** Extract `SiteVisitApp.zip` using a
   file manager, or download it directly into Termux.
3. **In Termux**, install git and set up a repo:
   ```bash
   pkg update && pkg install git
   cd storage/downloads/SiteVisitApp   # wherever you extracted it
   git init
   git add .
   git commit -m "Initial commit"
   ```
4. **Create an empty repo on GitHub** (github.com → New repository — don't
   initialize it with a README).
5. **Create a Personal Access Token** on GitHub (Settings → Developer settings
   → Personal access tokens → generate one with `repo` scope) — you'll use
   this as your password when pushing from Termux.
6. **Push the code:**
   ```bash
   git remote add origin https://github.com/<your-username>/<repo-name>.git
   git branch -M main
   git push -u origin main
   ```
   (When prompted for a password, paste the token from step 5.)
7. **GitHub Actions builds automatically** on push — go to your repo's
   **Actions** tab in a browser (works fine on mobile Chrome) and watch the
   `Build APK` workflow run (~2-3 minutes).
8. When it finishes, open the workflow run and download the
   **SiteVisit-debug-apk** artifact — it's a zip containing `app-debug.apk`.
9. **Install the APK** on your phone: open the downloaded file, allow
   "install from unknown sources" if prompted, and install.

From then on, any time you want a new build (after making changes and asking
me to update the code), just `git add . && git commit -m "update" && git push`
from Termux and a fresh APK will be waiting in the Actions tab a few minutes
later.

### If you'd rather I build it differently

I can also restructure this as a **web app (PWA)** instead — I can build and
show you a fully working version directly in this chat, installable to your
home screen with no GitHub/compiling step at all. It won't have quite the same
native feel (e.g. camera/GPS access is a bit more limited), but it works
entirely on-device with zero setup. Just say the word if you'd rather go that
route instead.



- **Android Studio** Iguana (2023.2.1) or newer
- **JDK 17** (bundled with recent Android Studio versions)
- A device or emulator running **Android 8.0 (API 26)** or newer

## Opening the project

1. Unzip this project.
2. Open Android Studio → **File → Open** → select the unzipped `SiteVisitApp` folder.
3. Let Gradle sync (first sync will download dependencies — needs internet).
4. Run on an emulator or a physical device via the green ▶ Run button.

The project uses the Gradle plugin/AGP versions declared in `build.gradle.kts`;
if Android Studio prompts you to upgrade AGP, you can accept it or leave it —
either works fine.

## Permissions

On first launch the app requests:
- **Location** (fine + coarse) — for the "use current GPS location" button
- **Camera** — for taking site photos
- **Notifications** — for visit reminders (Android 13+)

You can also grant "Allow exact alarms" in system settings if you want visit
reminders to fire at the precise minute rather than within a small window
(the app falls back gracefully if this isn't granted).

## Notes on the map

This build uses **osmdroid** (OpenStreetMap) so the app runs immediately with
no setup. If you'd rather use **Google Maps** (e.g. for satellite view or
Street View), that's a straightforward swap:
1. Add `com.google.maps.android:maps-compose` + `com.google.android.gms:play-services-maps` to `app/build.gradle.kts`.
2. Get a free Maps SDK API key from the Google Cloud Console and add it to
   `AndroidManifest.xml`.
3. Replace the `MapView`/`AndroidView` block in `MapScreen.kt` with a Compose
   `GoogleMap` composable and `Marker` calls.

Happy to make that swap for you if you'd like — just ask.

## Project structure

```
app/src/main/java/com/sitevisit/app/
├── data/            Room entities, DAOs, database, repository
├── reminders/        AlarmManager scheduling + notification receiver
├── ui/
│   ├── navigation/    Bottom-nav shell + NavHost routes
│   ├── screens/       All screens (sites, map, schedule, quotations, etc.)
│   └── theme/         Compose Material3 theme
├── util/              Currency/date formatting, GPS location helper
├── viewmodel/         AppViewModel — single source of UI state/actions
├── MainActivity.kt
└── SiteVisitApplication.kt
```

## Known limitations / things you may want to extend

- Quotations don't yet export to PDF — that's a natural next step using
  Android's `PdfDocument` API, or a "share as text" action in the meantime.
- No cloud sync/backup — data lives only on the device it's installed on.
- The map picker for site location is GPS-capture only (stand at the site and
  tap the button); there's no drag-a-pin-on-a-map picker yet.
- No multi-user/auth — this is a single-user local tool as built.

If you want any of these added, or want the Google Maps swap, just ask.
