/*
 * Copyright (C) 2013 salesforce.com, inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
Function.RegisterNamespace("Test.Aura.Controller");

[ Fixture ]
Test.Aura.Controller.ActionCallbackGroupTest = function() {
	// #import aura.controller.ActionCallbackGroup

	var MockAction = function(id) {
		this.id = id;
	};
	MockAction.prototype.addCallbackGroup = function() {
	};
	var mockWindow = Mocks.GetMock(Object.Global(), "window", {});

	var mockAuraUtil = Mocks.GetMock(Object.Global(), "$A", {
	    util : {
			arrayIndexOf : function(array, searchElement) {
			    for (var i = 0; i < array.length; i++) {
			      if (array[i] === searchElement) {
			        return i;
			      }
			    }
			    return -1;
			}
	    }
	});
	
	[Fixture]
	function Constructor() {
		[ Fact ]
		function SetsScope() {
			var expected = "expected";

			var actual = new ActionCallbackGroup([], expected, function() {
			}).scope;

			Assert.Equal(expected, actual);
		}

		[ Fact ]
		function SetsCallback() {
			var expected = function() {
				return "expected";
			};
			var actual;

			mockWindow(function() {
			    actual = new ActionCallbackGroup([], null, expected).callback;
			});

			Assert.Equal(expected, actual);
		}

		[ Fact ]
		function ExecutesCallbackIfActionsEmpty() {
			var stubbedCallback = Stubs.GetMethod("param", null);
			
			mockWindow(function() {
			    new ActionCallbackGroup([], null, stubbedCallback);
			});
			
			Assert.Equal([ {
				Arguments : {
					param : {
						errors : []
					}
				},
				ReturnValue : null
			} ], stubbedCallback.Calls);
		}

		[ Fact ]
		function NoErrorIfCallbackUndefined() {
			// because it would normally try to execute the callback if actions was empty
			new ActionCallbackGroup([], null);
		}

		[ Fact ]
		function NoErrorIfCallbackNull() {
			// because it would normally try to execute the callback if actions was empty
			new ActionCallbackGroup([], null, null);
		}

		[ Fact ]
		function ExecutesCallbackWithProvidedScope() {
			var scope = {};
			var callback = function() {
				this.property = "expected"
			};
			var expected = {
				property : "expected"
			};

			new ActionCallbackGroup([], scope, callback);

			Assert.Equal(expected, scope);
		}

		[ Fact ]
		function ExecutesCallbackWithWindowScopeIfScopeUndefined() {
			var expected = "expected";
			var callback = function() {
				this.property = expected;
			};
			var actual;

			mockWindow(function() {
				new ActionCallbackGroup([], undefined, callback);
				actual = window.property;
			});

			Assert.Equal(expected, actual);
		}

		[ Fact ]
		function ExecutesCallbackWithWindowScopeIfScopeNull() {
			var expected = "expected";
			var callback = function() {
				this.property = expected;
			};
			var actual;

			mockWindow(function() {
				new ActionCallbackGroup([], null, callback);
				actual = window.property;
			});

			Assert.Equal(expected, actual);
		}

		[ Fact ]
		function SetsEmptyActionsIfActionsEmpty() {
		    	var actual;
		    
		    	mockWindow(function() {
        			actual = new ActionCallbackGroup([], null, function() {
        			}).actions;
		    	});

			Assert.Equal([], actual);
		}

		[ Fixture ]
		function WithActions() {
			[ Fact ]
			function CopiesActionWithAddCallbackGroup() {
				var expected = [ new MockAction("expected") ];

				var actual = new ActionCallbackGroup(expected, null, function() {
				}).actions;

				Assert.Equal(expected, actual);
			}

			[ Fact ]
			function CopiesMultipleActions() {
				var valid1 = new MockAction("valid1");
				var valid2 = new MockAction("valid2");
				var actions = [ valid1, valid2 ];
				var expected = [ valid1, valid2 ];

				var actual = new ActionCallbackGroup(actions, null, function() { }).actions;

				Assert.Equal(expected, actual);
			}

			[ Fact ]
			function CallsAddCallbackGroupOnAction() {
				var stub = Stubs.GetMethod("group", null);
				var actions = [ {
					addCallbackGroup : stub
				} ];

				var expected = new ActionCallbackGroup(actions, null, function() {
				});

				Assert.Equal([ {
					Arguments : {
						group : expected
					},
					ReturnValue : null
				} ], stub.Calls);
			}

			[ Fact ]
			function DoesNotExecuteCallbackIfActionsNotEmpty() {
				var stubbedCallback = Stubs.GetMethod("param", null);

				new ActionCallbackGroup([ {
					addCallbackGroup : function() {
					}
				} ], null, stubbedCallback);

				Assert.Equal(0, stubbedCallback.Calls.length);
			}
		}
	}

	[ Fixture ]
	function CompleteAction() {

		[ Fact ]
		function DoesNothingIfActionNotFound() {
			var action = new MockAction("inlist");
			var stubbedCallback = Stubs.GetMethod("param", null);
			var group = new ActionCallbackGroup([ action ], null, stubbedCallback);
			var other = new MockAction("other");

			mockAuraUtil(function() { group.completeAction(other) });

			Assert.Equal(0, stubbedCallback.Calls.length);
			Assert.Equal([ action ], group.actions);
		}

		[ Fact ]
		function RemovesActionFromList() {
			var action = new MockAction();
			var scope = {};
			var callback = function() {
			};
			var group = new ActionCallbackGroup([ action ], scope, callback);

			mockAuraUtil(function() { group.completeAction(action) });

			Assert.Equal([], group.actions);
		}

		[ Fact ]
		function RemovesActionFromListDuringConstruction() {
			var action = new MockAction();
			var scope = {};
			var callback = function() {
			};
			var group = new ActionCallbackGroup([ action ], scope, callback);
			group.hold = true;

			mockAuraUtil(function() { group.completeAction(action) });

			Assert.Equal([], group.actions);
		}

		[ Fact ]
		function DoesNotExecuteCallbackIfActionsNotEmpty() {
			var action1 = new MockAction("inlist1");
			var action2 = new MockAction("inlist2");
			var stubbedCallback = Stubs.GetMethod("param", null);
			var group = new ActionCallbackGroup([ action1, action2 ], null, stubbedCallback);

			mockAuraUtil(function() { group.completeAction(action1) });

			Assert.Equal(0, stubbedCallback.Calls.length);
		}

		[ Fact ]
		function DoesNotExeuteCallbackDuringConstruction() {
			var action = new MockAction();
			var stubbedCallback = Stubs.GetMethod("param", null);
			var group = new ActionCallbackGroup([ action ], null, stubbedCallback);
			group.hold = true;

			mockAuraUtil(function() { group.completeAction(action) });

			Assert.Equal(0, stubbedCallback.Calls.length);
		}

		[ Fact ]
		function ExecutesCallbackIfLastActionCompleted() {
		    	//Arrange
			var action = new MockAction();
			var stubbedCallback = Stubs.GetMethod("param", null);
			var group;
			mockWindow(function() {
			    group = new ActionCallbackGroup([ action ], null, stubbedCallback);
			});
			
			//Act
			mockWindow(function(){
				mockAuraUtil(function() { group.completeAction(action) });
			});

			//Assert
			Assert.Equal([ {
				Arguments : {
					param : {
						errors : []
					}
				},
				ReturnValue : null
			} ], stubbedCallback.Calls);
		}

		[ Fact ]
		function NoErrorIfCallbackUndefined() {
			// because it would normally try to execute the callback if last action was completed
			var action = new MockAction();
			var stubbedCallback = Stubs.GetMethod("param", null);
			var group = new ActionCallbackGroup([ action ], null);

			mockAuraUtil(function() { group.completeAction(action) });
		}

		[ Fact ]
		function NoErrorIfCallbackNull() {
			// because it would normally try to execute the callback if last action was completed
			var action = new MockAction();
			var stubbedCallback = Stubs.GetMethod("param", null);
			var group = new ActionCallbackGroup([ action ], null, null);

			mockAuraUtil(function() { group.completeAction(action) });
		}

		[ Fact ]
		function ExecutesCallbackWithProvidedScope() {
			var action = new MockAction();
			var scope = {};
			var callback = function() {
				this.property = "expected"
			};
			var expected = {
				property : "expected"
			};
			var group = new ActionCallbackGroup([ action ], scope, callback);

			mockAuraUtil(function() { group.completeAction(action) });

			Assert.Equal(expected, scope);
		}

		[ Fact ]
		function ExecutesCallbackWithWindowScopeIfScopeUndefined() {
			var action = new MockAction();
			var expected = "expected";
			var callback = function() {
				this.property = expected;
			};
			var group = new ActionCallbackGroup([ action ], undefined, callback);
			var actual;

			mockWindow(function() {
				mockAuraUtil(function() { group.completeAction(action) });
				actual = window.property;
			});

			Assert.Equal(expected, actual);
		}

		[ Fact ]
		function ExecutesCallbackWithWindowScopeIfScopeNull() {
			var action = new MockAction();
			var expected = "expected";
			var callback = function() {
				this.property = expected;
			};
			var group = new ActionCallbackGroup([ action ], null, callback);
			var actual;

			mockWindow(function() {
				mockAuraUtil(function() { group.completeAction(action) });
				actual = window.property;
			});

			Assert.Equal(expected, actual);
		}
	}
}
