class HaxeLimeLanguage {
	static public function getLanguage(): String {
		#if android return getLanguageAndroid();
		#elseif ios return getLanguageIOS();
		#elseif windows return getLanguageWindows();
		#else return 'unknown-unknown';
		#end
	}

	#if android
	static private function getLanguageAndroid(): String {
		var getDefaultLocale = lime.system.JNI.createStaticMethod("java/util/Locale", "getDefault", "()Ljava/util/Locale;");
		var locale = getDefaultLocale();
		var getLanguage = lime.system.JNI.createMemberMethod("java/util/Locale", "getLanguage", "()Ljava/lang/String;");
		var language = getLanguage(locale);
		trace('HaxeLimeLanguage: Android: Detected language: $language');
		return language;
	}
	#end

	#if ios
	static private function getLanguageIOS(): String {
		return 'unknown-ios';
	}
	#end

	#if windows
	static private function getLanguageWindows(): String {
		return 'unknown-windows';
	}
	#end
}