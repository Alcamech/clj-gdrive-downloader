# clj-gdrive-downloader

Minimal utility to download shared compressed files from Google Drive.

Supported filetypes: 
* `.zip`     
* `.tgz`

## Usage
Downloads a shared compressed file from google drive into a given folder. 
Optionally decompresses it. 

   Arguments:
   - filename: name + extensions you want to save the file as
   - dest-path: the destination to save the downloaded file
   - url: google drive download url
   - overwrite: optional, if true forces re-download and overwrite
   - extract: optional, if true will extract a compressed a file

```clojure
(download-file-from-google-drive "tokenize.zip" "/path/to/dest/" "https://drive.google.com/link")
Downloading tokenize.zip into /path/to/dest/
File successfully downloaded.
=> nil
```
## TODO 

Add support for additional filetypes

* bzip2 
* gunzip 
* xz 

## License

Copyright © 2020 Lawton C. Mizell

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
