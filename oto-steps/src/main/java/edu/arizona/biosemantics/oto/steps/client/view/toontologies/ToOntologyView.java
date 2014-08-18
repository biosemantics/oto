package edu.arizona.biosemantics.oto.steps.client.view.toontologies;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.oto.steps.client.presenter.toontologies.ToOntologyPresenter;

public class ToOntologyView extends Composite implements ToOntologyPresenter.Display {
	/**
	 * things on the left list part
	 */
	private int regularStructureCount = 0;
	private int regularCharacterCount = 0;
	private int removedStructureCount = 0;
	private int removedCharacterCount = 0;

	private DisclosurePanel regularStrucureDisclosure;
	private DisclosurePanel regularCharacterDisclosure;
	private DisclosurePanel removedStructureDisclosure;
	private DisclosurePanel removedCharacterDisclosure;

	private VerticalPanel regularStructureList;
	private VerticalPanel regularCharacterList;
	private VerticalPanel removedStructureList;
	private VerticalPanel removedCharacterList;

	private Image updateMatchingStatusBtn;

	/**
	 * things in the middle part: matches and submissions
	 */
	private ScrollPanel middlePanel;
	private ScrollPanel rightPanel;
	private LayoutPanel layout;
	private TextBox localOntoName;
	private Button newSubmisionBtn;
	private TextBox localOntoPrefix;
	private HorizontalPanel localOnto;
	private Button createOntBtn;
	private Label queryLabel;
	
	public ToOntologyView() {

		layout = new LayoutPanel();
		initWidget(layout);

		//top part: instruction, new ontology new and prefix
		VerticalPanel top = new VerticalPanel();
		layout.add(top);
		layout.setWidgetTopHeight(top, 1, Unit.PCT, 15, Unit.PCT);
		Label instruction1 = new Label();
		instruction1.setText("This is an optional step where you can organize your terms into a local ontology (specific for a taxon group) and submit your terms to some other ontologies via Bioportal. You can skip this step by clicking on 'Next'.");
		top.add(instruction1);


		localOnto = new HorizontalPanel();
		setLocalOntoPanel();
		top.add(localOnto);

		// left list part
		/*ScrollPanel leftPart = new ScrollPanel();
		layout.add(leftPart);
		layout.setWidgetLeftRight(leftPart, 0, Unit.PCT, 82, Unit.PCT);
		layout.setWidgetTopBottom(leftPart, 15, Unit.PCT, 0, Unit.PCT);
	
		DecoratorPanel leftListPanel = new DecoratorPanel();
		leftPart.add(leftListPanel);

		leftListPanel.setWidth("100%");
		constructLeftListPart(leftListPanel);*/
		
		//left part
		/*DecoratorPanel leftListPanel = new DecoratorPanel();
		layout.add(leftListPanel);
		layout.setWidgetLeftRight(leftListPanel, 0, Unit.PCT, 60, Unit.PCT);
		layout.setWidgetTopBottom(leftListPanel, 15, Unit.PCT, 0, Unit.PCT);
		ScrollPanel leftPart = new ScrollPanel();	
		leftListPanel.add(leftPart);
		leftPart.setWidth("100%");
		VerticalPanel vPanel = new VerticalPanel();	
		leftPart.add(vPanel);
		constructLeftListPart(vPanel);*/
		ScrollPanel leftPart = new ScrollPanel();
		layout.add(leftPart);
		layout.setWidgetLeftRight(leftPart, 0, Unit.PCT, 82, Unit.PCT);
		layout.setWidgetTopBottom(leftPart, 15, Unit.PCT, 0, Unit.PCT);
		
		// left list part
		VerticalPanel leftListPanel = new VerticalPanel();
		leftPart.add(leftListPanel);

		leftListPanel.setWidth("100%");
		constructLeftListPart(leftListPanel);

		// middle part
		VerticalPanel middle = new VerticalPanel();
		
		HorizontalPanel hp = new HorizontalPanel();
		queryLabel = new Label("Ontologies containing the term: ");
		hp.add(queryLabel);
		hp.addStyleName("TO_ONTOLOGY_title");
		layout.add(hp);
		layout.setWidgetLeftRight(hp, 22, Unit.PCT, 40, Unit.PCT);
		layout.setWidgetTopHeight(hp, 15, Unit.PCT, 5, Unit.PCT);
		middlePanel = new ScrollPanel();
		middle.add(middlePanel);
		layout.add(middle);
		layout.setWidgetLeftRight(middle, 22, Unit.PCT, 40, Unit.PCT);
		layout.setWidgetTopBottom(middle, 20, Unit.PCT, 0, Unit.PCT);

		//initiateMiddlePanel();

		/**
		 * right detail part
		 */
		rightPanel = new ScrollPanel();
		layout.add(rightPanel);
		layout.setWidgetLeftRight(rightPanel, 62, Unit.PCT, 0, Unit.PCT);
		layout.setWidgetTopBottom(rightPanel, 15, Unit.PCT, 0, Unit.PCT);
	}
	@Override
	public void setLocalOntoPanel(){
		Label ontoName = new Label("Name your local ontology: ");
		//ontoName.addStyleName("field_label");
		localOntoName = new TextBox();
		localOntoName.setHeight("10px");
		localOntoName.setWidth("50px");
		localOntoName.setEnabled(true);
		localOnto.add(ontoName);
		localOnto.add(localOntoName);
		
		Label prefix = new Label("Prefix (All CAPITAL): ETC_");
		//prefix.addStyleName("field_label");
		localOntoPrefix = new TextBox();
		localOntoPrefix.setHeight("10px");
		localOntoPrefix.setWidth("50px");
		localOnto.add(prefix);
		localOnto.add(localOntoPrefix);
		
		createOntBtn = new Button ("Create New Ontology");
		createOntBtn.setTitle("create a new ontology with the name and prefix.");
		localOnto.add(createOntBtn);
	}
	
	@Override
	public Button getCreateOntologyButton(){
		return this.createOntBtn;
	}
	@Override
	public void setSize(String width, String height) {
		layout.setSize(width, height);
	}

	@Override
	public HorizontalPanel getLocalOntoPanel(){
		return this.localOnto;
	}
	
	@Override
	public Label getQueryLabel(){
		return this.queryLabel;
	}
	private void constructLeftListPart(VerticalPanel panel) {
		// list title and updateMatchingStatus button

		HorizontalPanel title_regular = new HorizontalPanel();
		title_regular.addStyleName("TO_ONTOLOGY_title");
		panel.add(title_regular);
		title_regular.add(new Label("Term (Category)"));

		updateMatchingStatusBtn = new Image("images/refresh.gif");
		updateMatchingStatusBtn
				.setTitle("refresh");
		updateMatchingStatusBtn.setHeight("20px");
		updateMatchingStatusBtn.addStyleName("right-align img_btn");
		title_regular.add(updateMatchingStatusBtn);

		// structure disclosure panel
		regularStrucureDisclosure = new DisclosurePanel("Structures");
		regularStrucureDisclosure.setWidth("100%");
		regularStrucureDisclosure.setAnimationEnabled(true);
		regularStructureList = new VerticalPanel();
		regularStructureList.setWidth("100%");
		regularStrucureDisclosure.setContent(regularStructureList);
		panel.add(regularStrucureDisclosure);

		// character disclosure panel
		regularCharacterDisclosure = new DisclosurePanel("Characters");
		regularCharacterDisclosure.setWidth("100%");
		regularCharacterDisclosure.setAnimationEnabled(true);
		regularCharacterList = new VerticalPanel();
		regularCharacterList.setWidth("100%");
		regularCharacterDisclosure.setContent(regularCharacterList);
		panel.add(regularCharacterDisclosure);

		// removed terms
		HorizontalPanel title_removed = new HorizontalPanel();
		title_removed.addStyleName("TO_ONTOLOGY_title");
		panel.add(title_removed);
		title_removed.add(new Label("Removed Terms"));

		// removed structure list
		removedStructureDisclosure = new DisclosurePanel("Removed Structures");
		removedStructureDisclosure.setWidth("100%");
		removedStructureDisclosure.setAnimationEnabled(true);
		removedStructureList = new VerticalPanel();
		removedStructureList.setWidth("100%");
		removedStructureDisclosure.setContent(removedStructureList);
		panel.add(removedStructureDisclosure);

		// removed character list
		removedCharacterDisclosure = new DisclosurePanel("Removed Characters");
		removedCharacterDisclosure.setWidth("100%");
		removedCharacterDisclosure.setAnimationEnabled(true);
		removedCharacterList = new VerticalPanel();
		removedCharacterList.setWidth("100%");
		removedCharacterDisclosure.setContent(removedCharacterList);
		panel.add(removedCharacterDisclosure);
	}

	@Override
	public Image getRefreshBtn() {
		return this.updateMatchingStatusBtn;
	}
	
	@Override
	public String getOntologyFileName(){
		return this.localOntoName.getText();
	}
	
	@Override
	public String getOntologyPrefix(){
		return this.localOntoPrefix.getText();
	}
	
	
	@Override
	public Button getNewSubmissionBtn() {
		return this.newSubmisionBtn;
	}
	public Widget asWidget() {
		return this;
	}

	/**
	 * use 1,2,3,4 to specify the 4 disclosure panels
	 */
	@Override
	public void updateTermCategoryPairsCount(ListType type, int count) {
		switch (type) {
		case REGULAR_STRUCTURE:
			this.regularStructureCount = count;
		this.regularStrucureDisclosure.getHeaderTextAccessor().setText(
					"Structures (" + Integer.toString(count) + ")");
			break;
		case REGULAR_CHARACTER:
			this.regularCharacterCount = count;
			this.regularCharacterDisclosure.getHeaderTextAccessor().setText(
					"Characters (" + Integer.toString(count) + ")");
			break;
		case REMOVED_STRUCTURE:
			this.removedStructureCount = count;
			this.removedStructureDisclosure.getHeaderTextAccessor().setText(
					"Removed Structures (" + Integer.toString(count) + ")");
			break;
		case REMOVED_CHARACTER:
			this.removedCharacterCount = count;
			this.removedCharacterDisclosure.getHeaderTextAccessor().setText(
					"Removed Characters (" + Integer.toString(count) + ")");
			break;
		default:
			break;
		}
	}

	@Override
	public VerticalPanel getListPanelByType(ListType type) {
		switch (type) {
		case REGULAR_STRUCTURE:
			return this.regularStructureList;
		case REGULAR_CHARACTER:
			return this.regularCharacterList;
		case REMOVED_STRUCTURE:
			return this.removedStructureList;
		case REMOVED_CHARACTER:
			return this.removedCharacterList;
		default:
			return null;
		}
	}

	@Override
	public VerticalPanel getRegularStructureList() {
		return this.regularStructureList;
	}

	@Override
	public VerticalPanel getRegularCharacterList() {
		return this.regularCharacterList;
	}

	@Override
	public VerticalPanel getRemovedStructureList() {
		return this.removedStructureList;
	}

	@Override
	public VerticalPanel getRemovedCharacterList() {
		return this.removedCharacterList;
	}

	@Override
	public int getListCountByType(ListType type) {
		switch (type) {
		case REGULAR_STRUCTURE:
			return this.regularStructureCount;
		case REGULAR_CHARACTER:
			return this.regularCharacterCount;
		case REMOVED_STRUCTURE:
			return this.removedStructureCount;
		case REMOVED_CHARACTER:
			return this.removedCharacterCount;
		default:
			return 0;
		}
	}

	@Override
	public SimplePanel getMiddlePanel() {
		return middlePanel;
	}

	@Override
	public SimplePanel getRightPanel() {
		return rightPanel;
	}

	@Override
	public void initiateMiddlePanel() {

	}

}
