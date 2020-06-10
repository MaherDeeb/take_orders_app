# Take Orders APP
It is an Android App that I wrote to take orders from the customers and establish invoices.
It is similar to the applications which restaurants use to get food orders from the visitors.
I designed the app for our family company that sells sawn wood and plywood.
The wood products have width, thickness, and length as main properties. 
The products can have extra specific features such as large or small, wet or dry, etc.
I wrote the App in Kotlin

## Requirements and installation

1. You need to download [Android Studio](https://developer.android.com/studio)
2. Clone the Repo

```shell script
$ git clone https://github.com/MaherDeeb/take_orders_app.git
```
3. Build APK

## App flow:


Check the Demo [here](https://youtu.be/8MdLv3h-j-o)

## Next Step

The App is a part of a data pipeline. The pipeline contains several components:

1. Wood orders taker App (this app): An android app that the workers use to collect orders from the customers. After confirming the payment, the App sends the data to the remote server.
2. Python-based remote server: It is a simple VM that receives and stores the data in the database. It sends a notification to keep the manger up to date after every transaction.
3. Invoices viewer App: It is a simple android app that the manager uses to view the notifications which the server sends. The manager can fetch the data from the server and check the invoices.
4. CenterAI: it is a computer vision framework that I wrote in Python on top of Pytorch. CenterAI enables training models for computer vision similar to other frameworks such as fastai but with a more convenient interface.
5. Wood QQ (not sure about the final name yet) App: It is an android app that the customer can use to count the sawn wood pieces and measure the wood quality from images.
6. An experimental environment to analyze the data. The data should answer some important business questions such as the next best offer for each customer,  the customer lifetime value, and so on.
7. A simple dashboard helps the manager observe how the company does in the short and long terms.

For more information you, check my [article](https://www.linkedin.com/pulse/ai-value-season-1-episode-maher-deeb)
