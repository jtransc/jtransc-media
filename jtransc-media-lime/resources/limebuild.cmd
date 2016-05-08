haxelib
run
lime
build
{{ actualSubtarget.name }}
{% if debug %}-debug{% else %}-release{% end %}
-Dsource-header=0

{% if actualSubtarget.name == "ios" %}
    ----
    # releasetype = release
    # releasetype = debug

    /usr/bin/xcrun
    -sdk
    iphoneos
    PackageApplication
    -v
    {{ buildFolder }}/export/{{ releasetype }}/ios/build/{{ releasetype|capitalize }}-iphoneos/{{ name }}.app
    -o
    {{ buildFolder }}/export/{{ releasetype }}/ios/build/{{ releasetype|capitalize }}-iphoneos/{{ name }}.ipa
{% end %}
