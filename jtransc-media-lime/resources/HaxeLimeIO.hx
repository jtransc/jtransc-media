class HaxeLimeIO {
    static public function readAsync(path:String, callback: Dynamic -> Dynamic -> Void) {
        #if sys
        try {
            var bytes = sys.io.File.getBytes(path);
            callback(null, JA_B.fromBytes(bytes));
        } catch (e:Dynamic) {
            callback(newException('Cannot read file "$path"'), null);
        }
        #elseif js
        var xhr = new js.html.XMLHttpRequest();
        xhr.open('GET', path, true);
        xhr.responseType = js.html.XMLHttpRequestResponseType.ARRAYBUFFER;
        xhr.onload = function(e) {
            var bytes = new haxe.io.UInt8Array(xhr.response);
            callback(null, HaxeArrayByte.fromUInt8Array(bytes));
        };
        xhr.onerror = function(e) {
            callback(newException('Cannot read file "$path"'), null);
        };
        xhr.send();
        #else
        callback(newException('Not implemented reading files "$path"'), null);
        #end
    }
}