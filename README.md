<p align="center">
  <h1 align="center">Company Vehicle Tracker</h1>

  <p align="center">
    simplify the car reporting process for the employee by
automating it!
    <br />
    <br />
<!--
    <a href="https://github.com/.../issues">Request Feature</a>
    ·
    <a href="https://github.com/.../issues">Report Bug</a>
-->
 </p>



<!-- TABLE OF CONTENTS -->
<details close="close">
  <summary>⚡ Table of Contents</summary>
  <ol>
    <li>
      <a href="#Before-this-Project">Before this Project</a>
      <ul>
        <li><a href="#The-problem">The problem</a></li>
      </ul>
    </li>
    <li>
      <a href="#After-this-Project">After this Project</a>
      <ul>
        <li><a href="#The-new-Application">The new Application</a></li>
      </ul>
    </li>
    <li><a href="#Running-The-Project">Running The Project</a>
      <ul>
        <li><a href="#Built-With">Built With</a></li>
      </ul>
   </li>
    <li><a href="#contact">Contact</a></li>

  </ol>
</details>

<!-- Before this PROJECT -->
## Before this Project

When the employee begins a ride and after he finishes it, he needs to
send a ride report via email to a designated email box. The report should contain the
following details:
1. The employee’s identity.
2. The car license plate number.
3. The date and time.
4. The odometer read.
5. The parking location.

### The problem

This reporting procedure has a few issues. It is tedious, thus sometimes forgotten or
ignored. It is prone to human errors and not all employees stick to a uniform email
format, which makes it harder to track report emails by eye. An additional issue is that
employees tend to be generic when they describe the parking spot, while we prefer it
to be as specific as possible, so the next employee who picks the car will find it easily.



<!-- After this PROJECT -->
## After this Project

we simplify the reporting process for the employee by automating it. In order to assist the automation of generating the reports on each car’s dashboard,
we will stick 2 stickers:
1. A sticker with the car’s license plate number as a [QR code](https://en.wikipedia.org/wiki/QR_code).
2. A sticker that bounds the odometer display with a red rectangle.

Now the reporting process can be very simple: The employee should take a picture of
the vehicle dashboard with both stickers and the odometer clearly visible, she then
sends the image to a designated email box and that’s all. No need to manually type in
anything, all the required information is already in attached to this email.


### The new Application

1. The input for the application will be a path to a textual [EML](https://www.loc.gov/preservation/digital/formats/fdd/fdd000388.shtml) report file, given as a command
line argument. The images attached to the emails will be in JPEG format, and they will be
   [Geotagged](https://en.wikipedia.org/wiki/Geotagging) by the sender.
   ![Report Example][report-example]

2. The application will process the report email and append all the required report
   data to a CSV “log” file.
   ![log Example][log-example]




   
<!-- Running THE PROJECT -->
## Running The Project

Few simple steps to run it:

1. Download the Vehicle Tracker zip folder.
2. Extract the Files from the downloaded file.
3. Open the folder [as IntelliJ IDEA project](https://www.jetbrains.com/help/idea/import-project-or-module-wizard.html).
4. Add a path to a valid EML file as a command line argument (few  EML files for example and testing in the main folder).
5. RUN.

### Built With

* [EML parser](https://www.programmersought.com/article/93981125979/)
* [ZXing (QR code)](https://github.com/zxing/zxing)
* [metadata directory](https://www.tabnine.com/code/java/methods/com.drew.metadata.Directory/getTags)
* [Tesseract OCR](https://www.geeksforgeeks.org/tesseract-ocr-with-java-with-examples/)



<!-- CONTACT -->
## Contact

Tal Hazi <> [talhazi114@gmail.com](mailto:talhazi114@gmail.com)


<!-- MARKDOWN LINKS & IMAGES -->
[report-example]: images/report_example.png
[log-example]: images/log_example.png
