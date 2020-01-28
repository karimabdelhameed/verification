<a href='https://bintray.com/bluecrunch/maven/bluecrunchverification/0.0.7/link'><img src='https://api.bintray.com/packages/bluecrunch/maven/bluecrunchverification/images/download.svg?version=0.0.7'></a> 
# Verification Module
<img src='https://miro.medium.com/max/3000/1*LCeoKUok8X5vfX4RS1FVhA.jpeg'>

You can now chill out on sending/verifying SMS using Firebase or even your server APIs on your android application.

It is normal for your Android application to have SMS verification screen (Register secnario , reset password , ... etc).
And it is very annoying that you do a lot of work every time.

So , we made verification module✌🏽to help you in that silly work.

# Features
<li>Send SMS using firebase.</li>
<li>Verify SMS using firebase.</li>
<li>Send SMS using server APIs (Get or post).</li>
<li>Verify SMS using server APIs (Get or post).</li>
<li>Control number of SMS verification digits (4,5 or 6).</li>
<li>Control box background (i.e. custom bg like rounded box or line ,...).</li>
<li>Control digit box size & spaces.</li>
<li>Control digit box height & text color.</li>

# Sceenshots
<p float="left">
<img src='https://imgbbb.com/images/2020/01/18/1c5d2dc47c51318f1.png' width="250"/>
<img src="https://imgbbb.com/images/2020/01/18/32ab32c79c96a8ad2.png" width="250"/>
<img src="https://imgbbb.com/images/2020/01/18/267257def58765819.png" width="250"/>
</p>

<img src="https://media.giphy.com/media/XGDrYnfDdJZAVSTaUu/giphy.gif" width="250"/>

# Download 
Maven
<pre>
<code>
&lt;dependency&gt;
  &lt;groupId&gt;com.bluecrunch&lt;/groupId&gt;
  &lt;artifactId&gt;bluecrunchverification&lt;/artifactId&gt;
  &lt;version&gt;0.0.7&lt;/version&gt;
&lt;/dependency&gt;
</code>
</pre>
Gradle
<pre>
<code>
implementation 'com.bluecrunch:bluecrunchverification:0.0.7'
</code>
</pre>

# Usage
You need to use the widget first in the XML layout of your verification layout.
<pre>
<code>
  &lt;com.bluecrunch.bluecrunchverification.VerificationView
            android:id="@+id/verification_layout"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginStart="50dp"
            android:layout_marginTop="20dp"
            android:layout_marginEnd="50dp"
            app:box_count="6"
            app:box_height="50dp"
            app:box_background="@drawable/line_shape"
            app:box_text_color="@color/colorAccent"
            app:box_space="7dp"
            app:layout_constraintTop_toTopOf="parent" /&gt;
</code>
</pre>
In your activity/fragment, just make a lateinit var from class called VerificationIntegration like this :
<pre><code>private lateinit var integration : VerificationIntegration</code></pre>

after that you need to build your integration object using builder pattern 🤠 like this: 

if you are using <strong>Firebase</strong> : 
<pre>
<code>
integration =
      VerificationIntegration
          .Builder(this)
          .setIsFirebase(true)
          .setCountryCode("country code here")
          .setFCMCallBack(this)
          .setMobileNumber("mobile number here")
          .build()
</code>
</pre>

and if you are using <strong>Server APIs</strong> : 
<pre>
<code>
integration =
      VerificationIntegration
          .Builder(this)
          .setIsFirebase(false)
          .setIsSendMethodGet(false)
          .setSendRequestBody("request body as JsonObject") // if it is a @POST method .
           // Sample for <strong>@POST</strong> request
           // val mRequest = TreeMap&lt;String, Any&gt;()
           // mRequest["to"] = "0000000000"
           // mRequest["message"] = "Welcome :D"
          .setWebserviceCallBack(this)
          .setSendRequestURL("web server url with end point here") // https://IP/sendSMS/ {params} . if using @GET
          // Sample for <strong>@GET</strong> request in the url 
          // https://IP/sendSMS/mobile=0000000000
          .build()
</code>
</pre>

Finally 🥳, you just need to call send SMS or verify SMS methods : 
<pre>
<code>
 integration.sendFCMSms() 
 // Or
 integration.sendSMSPOST()
</code>
</pre>

# Special thanks 
@sherifMohammed95 , and
to all <a href='https://www.bluecrunch.com/'>Bluecrunch</a> team specially the mobile team 💪🏻.

# Licence 
Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with the License. You may obtain a copy of at:
<a href='https://opensource.org/licenses/apache2.0.php'>https://opensource.org/licenses/apache2.0.php</a>

# Thank you ❤️
