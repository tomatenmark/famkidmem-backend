# FamKidMem
WebApp and Tools to share Videos online, but securely encrypted. One Admin/Editor, Many Users/Whatchers.

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
You will need a file named **ccms_auth_token_hash** in same directory like the jar file.\
This file has to contain a bcrypt hash of desired auth_token for /ccms/... paths.\
This is needed to authorize the ccms application.\
The ccms application has to send the auth_token in header **CCMS-AUTH-TOKEN**

# All Repos for FamKidMem
* Web-Backend: https://github.com/tomatenmark/famkidmem-backend
* Control & Content-Management-System (CCMS): https://github.com/tomatenmark/famkidmem-ccms
* Frontend: https://github.com/tomatenmark/famkidmem-frontend

# Security Architecture
https://famkidmem.de/security-architecture.pdf

# License
CC BY-SA 4.0: https://creativecommons.org/licenses/by-sa/4.0/

# Disclaimer
**This software is for privacy, not to hide crime. Please do not use it to encrypt your illegal contents. Thank you**

# Credits
* Spring boot and Vue.js
* javax.crypto and crypto-js
* FFmpeg and HLS
