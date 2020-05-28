# FamKidMem (Familien-Kindheits-Erinnerungen)
Remember those 'selbstgedrehte videos' from mid ninetees?\
Maybe you have them on a stick or your pc, but do you know where to look for it?\
Are you sure, you not lost it?\
Imagine you could always watch this videos and experience childhood memories with just one click in the internet.\
This is what this web based application does.\
You don't even have to turn your tower pc on. You can watch the videos online on your smartphone (better via wlan)

# Backend
This repository contains the sources for the FamKidMem Web-backend.\


# Features
* **User-Management**
  * **Login/Logout**  (/api/user)
  * **Maintain Users** (/cms/admin)
* **Add, update and delete Videos** (/cms/edit/video/ | encrypted)
* **Get Video Index, Thumbnails, m3u8 files and ts files** (/api/video and /api/ts | encrypted)

# Documentation
**/swagger-ui.html**

# Build
**mvn clean package**

# Run
**java -jar famkidmem-web-backend...jar [--files-dir <path-to-files>]**\
\# where path to files is: Path to directory where the files (thumbnails, m3u8, ts) should be stored.

# ApiKey
You will a need a file named **ccms_auth_token_hash** in same directory like the jar file.\
This file has to contain a bcrypt hash of desired auth_token for /ccms/... paths.\
This is needed to authorize the ccms application.\
The ccms application has to send the auth_token in header **CCMS_AUTH_TOKEN**

# All Repos for FamKidMem
* Web-Backend: https://github.com/tomatenmark/famkidmem-backend
* Control & Content-Management-System (CCMS): https://github.com/tomatenmark/famkidmem-ccms
* Frontend: https://github.com/tomatenmark/famkidmem-frontend

# Security Architecture
https://cloud.markherrmann.de/index.php/s/DoK6MV7uHZx0wy2