(function() {
    window.WallTime || (window.WallTime = {});
    window.WallTime.data = {
        rules: {"Taiwan":[{"name":"Taiwan","_from":"1945","_to":"1951","type":"-","in":"May","on":"1","at":"0:00","_save":"1:00","letter":"D"},{"name":"Taiwan","_from":"1945","_to":"1951","type":"-","in":"Oct","on":"1","at":"0:00","_save":"0","letter":"S"},{"name":"Taiwan","_from":"1952","_to":"only","type":"-","in":"Mar","on":"1","at":"0:00","_save":"1:00","letter":"D"},{"name":"Taiwan","_from":"1952","_to":"1954","type":"-","in":"Nov","on":"1","at":"0:00","_save":"0","letter":"S"},{"name":"Taiwan","_from":"1953","_to":"1959","type":"-","in":"Apr","on":"1","at":"0:00","_save":"1:00","letter":"D"},{"name":"Taiwan","_from":"1955","_to":"1961","type":"-","in":"Oct","on":"1","at":"0:00","_save":"0","letter":"S"},{"name":"Taiwan","_from":"1960","_to":"1961","type":"-","in":"Jun","on":"1","at":"0:00","_save":"1:00","letter":"D"},{"name":"Taiwan","_from":"1974","_to":"1975","type":"-","in":"Apr","on":"1","at":"0:00","_save":"1:00","letter":"D"},{"name":"Taiwan","_from":"1974","_to":"1975","type":"-","in":"Oct","on":"1","at":"0:00","_save":"0","letter":"S"},{"name":"Taiwan","_from":"1979","_to":"only","type":"-","in":"Jun","on":"30","at":"0:00","_save":"1:00","letter":"D"},{"name":"Taiwan","_from":"1979","_to":"only","type":"-","in":"Sep","on":"30","at":"0:00","_save":"0","letter":"S"}]},
        zones: {"Asia/Taipei":[{"name":"Asia/Taipei","_offset":"8:06:00","_rule":"-","format":"LMT","_until":"1896"},{"name":"Asia/Taipei","_offset":"8:00","_rule":"Taiwan","format":"C%sT","_until":""}]}
    };
    window.WallTime.autoinit = true;
}).call(this);
