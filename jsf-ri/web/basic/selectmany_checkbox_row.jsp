				<tr>
					<td>Multi-select checkbox:</td>
					<td><h:selectmany_checkboxlist id="ManyApples3">
						<h:selectitem itemValue="0" itemLabel="zero" />
						<h:selectitem itemValue="1" itemLabel="one" />
						<h:selectitem itemValue="2" itemLabel="two" />
						<h:selectitem itemValue="3" itemLabel="three" />
						<h:selectitem itemValue="4" itemLabel="four" selected="true" />
						<h:selectitem itemValue="5" itemLabel="five" />
						<h:selectitem itemValue="6" itemLabel="six" />
						<h:selectitem itemValue="7" itemLabel="seven" selected="true" />
						<h:selectitem itemValue="8" itemLabel="eight" />
						<h:selectitem itemValue="9" itemLabel="nine" />
					</h:selectmany_checkboxlist></td>
				</tr>
				<tr>
					<td>Multi-select checklistmodel:</td>
					<td><h:selectmany_checkboxlist id="checklistmodel"
						modelReference="LoginBean.currentOptions">
						<h:selectitems id="checklistmodelitems"
							modelReference="LoginBean.options" />
					</h:selectmany_checkboxlist></td>
				</tr>
