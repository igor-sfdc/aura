({
	testLabelAsAttribute : {
		test : function(cmp) {
			$A.test.assertEquals("Today", cmp.find('LabelAsAttribute').get(
					'v.class'));
		}
	},
	testLabelAsExpressionComponent : {
		test : function(cmp) {
			$A.test
					.assertEquals("Today + Overdue", $A.test
							.getTextByComponent(cmp
									.find('LabelAsExpressionComponent')));
		}
	}
})