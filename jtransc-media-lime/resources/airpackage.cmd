#adt
#-package
#-storetype
#pkcs12
#-keystore
#dummy.p12
#-storepass
#dummy
#{{ name }}.air
#app.xml
#{{ name }}.swf
#assets

----
adt
-package
-target
	ipa-app-store
-storetype
	pkcs12
-keystore
	../AppleDistribution.p12
-provisioning-profile
	AppleDistribution.mobileprofile
myApp.ipa
air.xml
{{ name }}.swf
icons
Default.png
assets