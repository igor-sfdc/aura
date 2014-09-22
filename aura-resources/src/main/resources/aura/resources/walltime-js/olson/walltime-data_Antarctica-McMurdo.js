(function() {
    window.WallTime || (window.WallTime = {});
    window.WallTime.data = {
        rules: {"NZAQ":[{"name":"NZAQ","_from":"1974","_to":"only","type":"-","in":"Nov","on":"3","at":"2:00s","_save":"1:00","letter":"D"},{"name":"NZAQ","_from":"1975","_to":"1988","type":"-","in":"Oct","on":"lastSun","at":"2:00s","_save":"1:00","letter":"D"},{"name":"NZAQ","_from":"1989","_to":"only","type":"-","in":"Oct","on":"8","at":"2:00s","_save":"1:00","letter":"D"},{"name":"NZAQ","_from":"1990","_to":"2006","type":"-","in":"Oct","on":"Sun>=1","at":"2:00s","_save":"1:00","letter":"D"},{"name":"NZAQ","_from":"1975","_to":"only","type":"-","in":"Feb","on":"23","at":"2:00s","_save":"0","letter":"S"},{"name":"NZAQ","_from":"1976","_to":"1989","type":"-","in":"Mar","on":"Sun>=1","at":"2:00s","_save":"0","letter":"S"},{"name":"NZAQ","_from":"1990","_to":"2007","type":"-","in":"Mar","on":"Sun>=15","at":"2:00s","_save":"0","letter":"S"},{"name":"NZAQ","_from":"2007","_to":"max","type":"-","in":"Sep","on":"lastSun","at":"2:00s","_save":"1:00","letter":"D"},{"name":"NZAQ","_from":"2008","_to":"max","type":"-","in":"Apr","on":"Sun>=1","at":"2:00s","_save":"0","letter":"S"}]},
        zones: {"Antarctica/McMurdo":[{"name":"Antarctica/McMurdo","_offset":"0","_rule":"-","format":"zzz","_until":"1956"},{"name":"Antarctica/McMurdo","_offset":"12:00","_rule":"NZAQ","format":"NZ%sT","_until":""}]}
    };
    window.WallTime.autoinit = true;
}).call(this);
