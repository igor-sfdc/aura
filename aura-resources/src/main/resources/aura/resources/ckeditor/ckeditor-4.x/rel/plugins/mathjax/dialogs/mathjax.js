﻿/*
 Copyright (c) 2003-2014, CKSource - Frederico Knabben. All rights reserved.
 For licensing, see LICENSE.md or http://ckeditor.com/license
*/
CKEDITOR.dialog.add("mathjax",function(d){var c,b=d.lang.mathjax;return{title:b.title,minWidth:350,minHeight:100,contents:[{id:"info",elements:[{id:"equation",type:"textarea",label:b.dialogInput,onLoad:function(){var a=this;if(!(CKEDITOR.env.ie&&8==CKEDITOR.env.version))this.getInputElement().on("keyup",function(){c.setValue("\\("+a.getInputElement().getValue()+"\\)")})},setup:function(a){this.setValue(CKEDITOR.plugins.mathjax.trim(a.data.math))},commit:function(a){a.setData("math","\\("+this.getValue()+
"\\)")}},{id:"documentation",type:"html",html:'<div style="width:100%;text-align:right;margin:-8px 0 10px"><a class="cke_mathjax_doc" href="'+b.docUrl+'" target="_black" style="cursor:pointer;color:#00B2CE;text-decoration:underline">'+b.docLabel+"</a></div>"},!(CKEDITOR.env.ie&&8==CKEDITOR.env.version)&&{id:"preview",type:"html",html:'<div style="width:100%;text-align:center;"><iframe style="border:0;width:0;height:0;font-size:20px" scrolling="no" frameborder="0" allowTransparency="true" src="'+CKEDITOR.plugins.mathjax.fixSrc+
'"></iframe></div>',onLoad:function(){var a=CKEDITOR.document.getById(this.domId).getChild(0);c=new CKEDITOR.plugins.mathjax.frameWrapper(a,d)},setup:function(a){c.setValue(a.data.math)}}]}]}});
