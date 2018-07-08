<p align="center">
  <image height=150 width=150 src="https://github.com/mayuroks/android-mvp-realtime-chat/blob/master/images/logo_socket_chat_circle.png"></image>
</p>

<h1 align="center"> Socket Chat </h1>
<h4 align="center">A demo app to showcase Android MVP Realtime architecture.</h4>

Socket Chat is a demo app built using Android MVP Realtime architecture. You can read more about the architecture on my Medium article https://bit.ly/2KXcTan .

This app connects to Socket.IO chat room at https://socket-io-chat.now.sh. Suggestions for further improvements are most welcome.

## Screenshot
<p>
  <image width="25%" height="25%"  style="margin:40px;" src="https://github.com/mayuroks/android-mvp-realtime-chat/blob/master/images/socket_chat.png"></image>
</p>

## Quick Demo
Check out this video on YouTube to see the app in action.

[![IMAGE ALT TEXT](http://img.youtube.com/vi/BJ70gpBXBcU/0.jpg)](http://www.youtube.com/watch?v=BJ70gpBXBcU "Socket Chat - Android MVP Realtime architecture")


## Problem Statement
Most of us Android developers have created apps using the MVP architecture. A regular android app involves making HTTP API calls to the server, getting some data and rendering the view. Using these APIs, you can accomplish things like

 - Booking a cab
 - Ordering lunch
 - Transferring money etc.

As you can see, HTTP APIs are quite simple, scalable and you can get a lot of things done. Once you learn MVP with RxJava and Retrofit you are ready to take on the world with your amazing app. Yay!!

This feeling of awesomeness is quickly replaced by confusion the moment you try to build realtime apps like

 - Chat app
 - Multiplayer gaming app
 - Realtime stock price updates app etc.
 
## Limitations of Android MVP
When I say Android MVP, I am referring to the [Todo-MVP-RxJava example from googlesamples](https://github.com/googlesamples/android-architecture/tree/todo-mvp-rxjava/).

In this Android MVP example,

 - View is responsible for rendering the UI
 - Presenter is responsible for presenting the data
 - Repository is responsible for getting the data

This architecuture is based on request-response model, where the client requests for some resource from the server and the server sends back a response. But the other way round, where the server sends data to the client and the client handles the data, is not possible in the architecture. Simply because, the client is not actively listening for incoming data from the server.  
 
## Proposed Solution
So for the app to be realtime app, it should be able to send and receive events/data from the server. Based on this, if we try to draw an architecture, it would look some thing like this.

<p>
  <image width="100%" height="100%"  style="margin:40px;" src="https://github.com/mayuroks/android-mvp-realtime-chat/blob/master/images/realtime_android_architecture.png"></image>
</p>

This Android project is an working implementation of the proposed Android MVP Realtime architecture.

## Usage
If you want to try out the app, follow the below steps

 - Clone the repo
 - Open the project in Android Studio
 - Run it on your Android phone
 - In the web browser, open https://socket-io-chat.now.sh.
 
This way you can send message between the app and the web chatroom.

**NOTE:** Sometimes, when you join a Socket.IO chat room on web, it might be a different chat room than the one to which the app is connected. I don't know what causes this since I am not the developer of Socket.IO web chat room.  

## Disclaimer
This is a side project app to experiment/play around with Android framework. This is not a production app. 
So there is no guarantee that issues/feature-requests/enhancements will be worked upon.

## License
MIT

---
> Github [@mayuroks](https://github.com/mayuroks) ~ Twitter [@mayuroks](https://twitter.com/mayuroks) ~ Medium [@mayuroks](https://medium.com/@mayuroks)
