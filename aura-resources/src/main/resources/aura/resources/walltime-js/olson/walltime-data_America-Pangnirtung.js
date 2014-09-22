(function() {
    window.WallTime || (window.WallTime = {});
    window.WallTime.data = {
        rules: {"NT_YK":[{"name":"NT_YK","_from":"1918","_to":"only","type":"-","in":"Apr","on":"14","at":"2:00","_save":"1:00","letter":"D"},{"name":"NT_YK","_from":"1918","_to":"only","type":"-","in":"Oct","on":"27","at":"2:00","_save":"0","letter":"S"},{"name":"NT_YK","_from":"1919","_to":"only","type":"-","in":"May","on":"25","at":"2:00","_save":"1:00","letter":"D"},{"name":"NT_YK","_from":"1919","_to":"only","type":"-","in":"Nov","on":"1","at":"0:00","_save":"0","letter":"S"},{"name":"NT_YK","_from":"1942","_to":"only","type":"-","in":"Feb","on":"9","at":"2:00","_save":"1:00","letter":"W"},{"name":"NT_YK","_from":"1945","_to":"only","type":"-","in":"Aug","on":"14","at":"23:00u","_save":"1:00","letter":"P"},{"name":"NT_YK","_from":"1945","_to":"only","type":"-","in":"Sep","on":"30","at":"2:00","_save":"0","letter":"S"},{"name":"NT_YK","_from":"1965","_to":"only","type":"-","in":"Apr","on":"lastSun","at":"0:00","_save":"2:00","letter":"DD"},{"name":"NT_YK","_from":"1965","_to":"only","type":"-","in":"Oct","on":"lastSun","at":"2:00","_save":"0","letter":"S"},{"name":"NT_YK","_from":"1980","_to":"1986","type":"-","in":"Apr","on":"lastSun","at":"2:00","_save":"1:00","letter":"D"},{"name":"NT_YK","_from":"1980","_to":"2006","type":"-","in":"Oct","on":"lastSun","at":"2:00","_save":"0","letter":"S"},{"name":"NT_YK","_from":"1987","_to":"2006","type":"-","in":"Apr","on":"Sun>=1","at":"2:00","_save":"1:00","letter":"D"}],"Canada":[{"name":"Canada","_from":"1918","_to":"only","type":"-","in":"Apr","on":"14","at":"2:00","_save":"1:00","letter":"D"},{"name":"Canada","_from":"1918","_to":"only","type":"-","in":"Oct","on":"27","at":"2:00","_save":"0","letter":"S"},{"name":"Canada","_from":"1942","_to":"only","type":"-","in":"Feb","on":"9","at":"2:00","_save":"1:00","letter":"W"},{"name":"Canada","_from":"1945","_to":"only","type":"-","in":"Aug","on":"14","at":"23:00u","_save":"1:00","letter":"P"},{"name":"Canada","_from":"1945","_to":"only","type":"-","in":"Sep","on":"30","at":"2:00","_save":"0","letter":"S"},{"name":"Canada","_from":"1974","_to":"1986","type":"-","in":"Apr","on":"lastSun","at":"2:00","_save":"1:00","letter":"D"},{"name":"Canada","_from":"1974","_to":"2006","type":"-","in":"Oct","on":"lastSun","at":"2:00","_save":"0","letter":"S"},{"name":"Canada","_from":"1987","_to":"2006","type":"-","in":"Apr","on":"Sun>=1","at":"2:00","_save":"1:00","letter":"D"},{"name":"Canada","_from":"2007","_to":"max","type":"-","in":"Mar","on":"Sun>=8","at":"2:00","_save":"1:00","letter":"D"},{"name":"Canada","_from":"2007","_to":"max","type":"-","in":"Nov","on":"Sun>=1","at":"2:00","_save":"0","letter":"S"}]},
        zones: {"America/Pangnirtung":[{"name":"America/Pangnirtung","_offset":"0","_rule":"-","format":"zzz","_until":"1921"},{"name":"America/Pangnirtung","_offset":"-4:00","_rule":"NT_YK","format":"A%sT","_until":"1995 Apr Sun>=1 2:00"},{"name":"America/Pangnirtung","_offset":"-5:00","_rule":"Canada","format":"E%sT","_until":"1999 Oct 31 2:00"},{"name":"America/Pangnirtung","_offset":"-6:00","_rule":"Canada","format":"C%sT","_until":"2000 Oct 29 2:00"},{"name":"America/Pangnirtung","_offset":"-5:00","_rule":"Canada","format":"E%sT","_until":""}]}
    };
    window.WallTime.autoinit = true;
}).call(this);
