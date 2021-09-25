# GeoLogger
A android app, which can read GNSS data from the underlying android phone and present it as a satellites view map and save the Android GNSS Raw data -> Rinex 3.03 file into your phone.

# Introduction
Since the release of Xiaomi 8 with dual-band positioning module, major domestic manufacturers have followed up (personally, in fact, 
just Broadcom first produced dual-band chip, the cell phone manufacturers who first released was meaningless at all), 
many people are studying the effect of this cell phone dual-band positioning module. 
In this case, let's make an APP to analyze his data and convert it into international Rinex data, so that experts and software in the field of high precision can analyze it.

# Structure
The whole development and functions are as follows:
① To make up a Satellite Map from the NMEA data received by the phone.
② Find a professional document to transform the RAW data into Rinex's four elements: carrier, pseudorange, signal-to-noise ratio, Doppler.
③ According to the Rinex 3.03 documentation, add a Rinex file hearder and package each Epoch data. In order to storge a RInex3.03 file for your reference.


# Blog
FIrst, if you are interesting in the field， kindly follow my wechat and Official Accounts, which include the detailed introduction and implementation process.

http://mp.weixin.qq.com/s?__biz=MzkzNjE4MzI0Ng==&mid=2247483678&idx=1&sn=44c81742975a507e540877b4265f6caa&chksm=c2a3d5bff5d45ca9ae8f2df6f9a8b3a38509b9648122a9e46de3f656da39a5dbf64351d3a64e#rd

And also, The English version is available from here and I wrote about it on the official website I set up for our team. Sadly, our team no longer exists, so, in the future I will transform it into a vlog or webpage for my own new team. Stay hopeful, as the sun is shining and the future is bright.

https://bd.hi-target.com.cn/pages/blog/SmartPhone-caseStudy1.html
