package edu.arizona.biosemantics.oto.steps.client.presenter.toontologies;

import java.io.File;
import java.util.ArrayList;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.oto.steps.client.event.processing.ProcessingEndEvent;
import edu.arizona.biosemantics.oto.steps.client.event.processing.ProcessingStartEvent;
import edu.arizona.biosemantics.oto.steps.client.event.to_ontologies.AddNewSubmissionEvent;
import edu.arizona.biosemantics.oto.steps.client.event.to_ontologies.AddNewSubmissionEventHandler;
import edu.arizona.biosemantics.oto.steps.client.event.to_ontologies.BackToDetailViewEvent;
import edu.arizona.biosemantics.oto.steps.client.event.to_ontologies.BackToDetailViewEventHandler;
import edu.arizona.biosemantics.oto.steps.client.event.to_ontologies.ClearSelectionEvent;
import edu.arizona.biosemantics.oto.steps.client.event.to_ontologies.ClearSelectionEventHandler;
import edu.arizona.biosemantics.oto.steps.client.event.to_ontologies.CreateOntologyEvent;
import edu.arizona.biosemantics.oto.steps.client.event.to_ontologies.CreateOntologyEventHandler;
import edu.arizona.biosemantics.oto.steps.client.event.to_ontologies.DeleteSubmissionEvent;
import edu.arizona.biosemantics.oto.steps.client.event.to_ontologies.DeleteSubmissionEventHandler;
import edu.arizona.biosemantics.oto.steps.client.event.to_ontologies.EditSubmissionEvent;
import edu.arizona.biosemantics.oto.steps.client.event.to_ontologies.EditSubmissionEventHandler;
import edu.arizona.biosemantics.oto.steps.client.event.to_ontologies.MoveTermCategoryPairEvent;
import edu.arizona.biosemantics.oto.steps.client.event.to_ontologies.MoveTermCategoryPairEventHandler;
import edu.arizona.biosemantics.oto.steps.client.event.to_ontologies.OntologyRecordClickEvent;
import edu.arizona.biosemantics.oto.steps.client.event.to_ontologies.OntologyRecordClickEventHandler;
import edu.arizona.biosemantics.oto.steps.client.event.to_ontologies.OntologyRecordSelectChangedEvent;
import edu.arizona.biosemantics.oto.steps.client.event.to_ontologies.OntologyRecordSelectChangedEventHandler;
import edu.arizona.biosemantics.oto.steps.client.event.to_ontologies.SubmitSubmissionEvent;
import edu.arizona.biosemantics.oto.steps.client.event.to_ontologies.SubmitSubmissionEventHandler;
import edu.arizona.biosemantics.oto.steps.client.event.to_ontologies.TermCategoryPairSelectedEvent;
import edu.arizona.biosemantics.oto.steps.client.event.to_ontologies.TermCategoryPairSelectedEventHandler;
import edu.arizona.biosemantics.oto.steps.client.presenter.MainPresenter;
import edu.arizona.biosemantics.oto.steps.client.presenter.Presenter;
import edu.arizona.biosemantics.oto.steps.shared.rpc.OntologyFileService;
import edu.arizona.biosemantics.oto.steps.shared.rpc.OntologyFileServiceAsync;
import edu.arizona.biosemantics.oto.steps.shared.rpc.RPCResult;
import edu.arizona.biosemantics.oto.steps.shared.rpc.ToOntologiesService;
import edu.arizona.biosemantics.oto.steps.shared.rpc.ToOntologiesServiceAsync;
import edu.arizona.biosemantics.oto.steps.client.view.toontologies.EditSubmissionView;
import edu.arizona.biosemantics.oto.steps.client.view.toontologies.ListType;
import edu.arizona.biosemantics.oto.steps.client.view.toontologies.MatchDetailView;
import edu.arizona.biosemantics.oto.steps.client.view.toontologies.MatchSubmissionView;
import edu.arizona.biosemantics.oto.steps.client.view.toontologies.OperationType;
import edu.arizona.biosemantics.oto.steps.client.view.toontologies.SubmissionDetailView;
import edu.arizona.biosemantics.oto.steps.client.view.toontologies.TermCategoryPairView;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.MappingStatus;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.OntologyInfo;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.OntologyMatch;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.OntologyRecord;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.OntologyRecordType;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.OntologySubmission;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.TermCategoryLists;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.TermCategoryPair;

public class ToOntologyPresenter implements Presenter {
	public interface Display {
		/**
		 * left: list part
		 */
		Image getRefreshBtn();
		Button getNewSubmissionBtn();
		VerticalPanel getListPanelByType(ListType type);
		int getListCountByType(ListType type);
		VerticalPanel getRegularStructureList();
		VerticalPanel getRegularCharacterList();
		VerticalPanel getRemovedStructureList();
		VerticalPanel getRemovedCharacterList();
		void updateTermCategoryPairsCount(ListType type, int count);
		void initiateMiddlePanel();
		Button getCreateOntologyButton();
		//middle matches and submission part
		SimplePanel getMiddlePanel();
		//right: detail part
		SimplePanel getRightPanel();
		Widget asWidget();
		void setSize(String width, String height);
		//create new local ontology if none exists for the user
		HorizontalPanel getLocalOntoPanel();
		void setLocalOntoPanel();
		String getOntologyFileName();
		String getOntologyPrefix();
		Label getQueryLabel();
	}

	private final Display display;
	private ToOntologiesServiceAsync rpcService = GWT
			.create(ToOntologiesService.class);
	private OntologyFileServiceAsync ontologyFileService = GWT.create(OntologyFileService.class);
	private HandlerManager eventBus = new HandlerManager(null);
	private String selectedTerm;
	private String selectedCategory;
	private Widget selectedPairLabel;
	private final HandlerManager globalEventBus;
	private HasWidgets container;


	public ToOntologyPresenter(Display view, HandlerManager globalEventBus) {
		this.display = view;
		this.globalEventBus = globalEventBus;
		
		try {	
			ontologyFileService.getOntologyInfo("hong", new AsyncCallback<RPCResult<ArrayList<OntologyInfo>>>() { //TODO replace 'hong' with a real userid
				@Override
				public void onFailure(Throwable exception) {
					Window.alert("failed to fetch user ontology information");
				}

				@Override
				public void onSuccess(RPCResult<ArrayList<OntologyInfo>> result) {
					if(result.getData().size()>0){
						 //update AvailableOntologies
						ArrayList<String> localOntoPrefix = new ArrayList<String>();
						for(OntologyInfo info: result.getData()){
							localOntoPrefix.add(info.getOntologyPrefix());
						}
						MainPresenter.addLocalOntologies(localOntoPrefix);
					}else{
						Window.alert(result.getMessage());
					}
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		/*Label ontoName = new Label("Name/select your local ontology:");
		ontoName.addStyleName("field_label");
		localOntoName = new ListBox();
		localOntoName.setHeight("10px");
		localOntoName.setWidth("50px");
		localOntoName.setEnabled(true);
		localOntoName.setItemText(1, "test1");
		localOntoName.setItemText(2, "test2");
		localOnto.add(ontoName);
		localOnto.add(localOntoName);
		
		Label prefix = new Label("and Prefix:");
		prefix.addStyleName("field_label");
		localOntoPrefix = new TextBox();
		localOnto.add(prefix);
		localOnto.add(localOntoPrefix);
		*/
		}
	}

	protected void refresh() {
		container.clear();
		container.add(display.asWidget());
	}

	@Override
	public void go(HasWidgets container) {
		bindEvents();
		container.clear();
		container.add(display.asWidget());
		this.container = container;
		fetchTermsList();
	}

	@Override
	public void bindEvents() {
		display.getRefreshBtn().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				refreshMatchSubmissionsStatus();
			}
		});

		
		eventBus.addHandler(TermCategoryPairSelectedEvent.TYPE,
				new TermCategoryPairSelectedEventHandler() {

					@Override
					public void onTermCategoryPairSelected(
							TermCategoryPairSelectedEvent event) {
						TermCategoryPair data = event.getData();
						selectedTerm = data.getTerm();
						selectedCategory = data.getCategory();
						selectedPairLabel = event.getWidget();
						updateMatchesAndSubmissions(selectedTerm,
								selectedCategory);
					}
				});

		eventBus.addHandler(MoveTermCategoryPairEvent.TYPE,
				new MoveTermCategoryPairEventHandler() {
					@Override
					public void onMove(MoveTermCategoryPairEvent event) {
						moveTermCategoryPair(event.getData(), event.getWidget());
					}
				});

		eventBus.addHandler(AddNewSubmissionEvent.TYPE,
				new AddNewSubmissionEventHandler() {
					@Override
					public void onClick(AddNewSubmissionEvent event) {
						dispayAddNewSubmissionView(selectedTerm,
								selectedCategory);
					}
				});

		eventBus.addHandler(OntologyRecordSelectChangedEvent.TYPE,
				new OntologyRecordSelectChangedEventHandler() {

					@Override
					public void onSelect(OntologyRecordSelectChangedEvent event) {
						//change button label from "Submit to Ontology" to "Add to Local Ontology"
						//display.getMiddlePanel().getWidget()
						displayOntologyRecordDetail(event.getSelectedRecord()
								.getType(), event.getSelectedRecord().getId());
								

					}
				});

		eventBus.addHandler(OntologyRecordClickEvent.TYPE,
				new OntologyRecordClickEventHandler() {

					@Override
					public void onClick(OntologyRecordClickEvent event) {
						updateSelectedOntologyRecord(event.getRecordID(),
								event.getType());
					}
				});

		eventBus.addHandler(EditSubmissionEvent.TYPE,
				new EditSubmissionEventHandler() {

					@Override
					public void onClick(EditSubmissionEvent event) {
						new EditSubmissionPresenter(new EditSubmissionView(
								event.getSubmission(),
								OperationType.UPDATE_SUBMISSION), eventBus)
								.go(display.getRightPanel());
					}
				});

		eventBus.addHandler(DeleteSubmissionEvent.TYPE,
				new DeleteSubmissionEventHandler() {

					@Override
					public void onClick(DeleteSubmissionEvent event) {
						deleteSubmission(event.getSubmission());
					}
				});

		eventBus.addHandler(BackToDetailViewEvent.TYPE,
				new BackToDetailViewEventHandler() {

					@Override
					public void onClick(BackToDetailViewEvent event) {
						// display the detail view
						displayOntologyRecordDetail(
								OntologyRecordType.SUBMISSION, event
										.getSubmission().getSubmissionID());
					}
				});

		eventBus.addHandler(SubmitSubmissionEvent.TYPE,
				new SubmitSubmissionEventHandler() {
					@Override
					public void onSubmit(SubmitSubmissionEvent event) {
						submitSubmission(event.getSubmission(),
								event.getSubmissionType());
					}
				});

		/*eventBus.addHandler(UpdateLocalEvent.TYPE, new UpdateLocalEventHandler(){

			@Override
			public void onClick(UpdateLocalEvent event) {
				updateLocalOntology();
			}
			
		});*/
		
		
		display.getCreateOntologyButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				OntologyInfo info = new OntologyInfo(display.getOntologyFileName(), "ETC_"+display.getOntologyPrefix(), "local", MainPresenter.uploadInfo.getGlossaryTypeName()); //TODO: get taxonGroup information from ETC
				eventBus.fireEvent(new CreateOntologyEvent("hong", MainPresenter.uploadID, info)); //TODO: replace Hong with userID variable
			}
		});
		
		eventBus.addHandler(CreateOntologyEvent.TYPE,
				new CreateOntologyEventHandler() {
					@Override
					public void onSubmit(CreateOntologyEvent event) {
						createEmptyNewOntology(event.getOntologyInfo(),
								event.getUserID(), event.getUploadID());
					}
				});

		eventBus.addHandler(ClearSelectionEvent.TYPE,
				new ClearSelectionEventHandler() {

					@Override
					public void onClick(ClearSelectionEvent event) {
						clearSelection();
					}
				});

	}

	protected void createEmptyNewOntology(OntologyInfo ontoInfo, String userID, String uploadID) {
		try {
			ontologyFileService.newEmptyOntologyFile(userID, uploadID, ontoInfo.getOntologyFileName(), ontoInfo.getOntologyPrefix(), ontoInfo.getTaxonGroup(), 
					new AsyncCallback<RPCResult<OntologyInfo>>(){

						@Override
						public void onFailure(Throwable exception) {
							if(exception.toString().contains("Duplicate entry")){
								Window.alert("Prefix exists. Choose a different Prefix.");
							}
							Window.alert("Internal error. Ontology file not created");
						}

						@Override
						public void onSuccess(RPCResult<OntologyInfo> result) {
							if(result.getMessage().length()==0){
								Window.alert("Ontology "+result.getData().getOntologyPrefix()+" created successfully");		
								ArrayList<String> localOntoPrefix = new ArrayList<String>();
								localOntoPrefix.add(result.getData().getOntologyPrefix());
								MainPresenter.addLocalOntologies(localOntoPrefix);
							}else{
								Window.alert(result.getMessage());		
							}
						}
			});
		} catch (Exception e) {
			Window.alert("Internal error. Ontology file not created");
			e.printStackTrace();
		}
		
	}

	private void deleteSubmission(OntologySubmission submission) {
		globalEventBus.fireEvent(new ProcessingStartEvent(
				"Deleting submission ..."));
		rpcService.deleteSubmission(submission, MainPresenter.uploadID,
				new AsyncCallback<Void>() {

					@Override
					public void onSuccess(Void result) {
						globalEventBus.fireEvent(new ProcessingEndEvent());
						Window.alert("Submission Deleted. ");
						display.getRightPanel().clear();
						updateMatchesAndSubmissions(selectedTerm,
								selectedCategory);
					}

					@Override
					public void onFailure(Throwable caught) {
						globalEventBus.fireEvent(new ProcessingEndEvent());
						Window.alert("Server Error: failed in deleting a submission. \n\n"
								+ caught.getMessage());
					}
				});
	}

	/**
	 * submit a new or an update of submission to bioportal, update db
	 * accordingly
	 * 
	 * @param submission
	 * @param type
	 */
	private void submitSubmission(OntologySubmission submission,
			OperationType type) {
		if (type.equals(OperationType.NEW_SUBMISSION)) {
			globalEventBus.fireEvent(new ProcessingStartEvent(
					"submitting ..."));
		} else {
			globalEventBus.fireEvent(new ProcessingStartEvent(
					"updating ..."));
		}

		rpcService.submitSubmission(submission, MainPresenter.uploadID, type,
				new AsyncCallback<RPCResult<Boolean>>() {

					@Override
					public void onSuccess(RPCResult<Boolean> result) {
						globalEventBus.fireEvent(new ProcessingEndEvent());
						if(result.getMessage().trim().isEmpty()){
							Window.alert("submit success");
						}else{
							Window.alert(result.getMessage());
						}
						updateMatchesAndSubmissions(selectedTerm,
								selectedCategory);
					}

					@Override
					public void onFailure(Throwable caught) {
						globalEventBus.fireEvent(new ProcessingEndEvent());
						Window.alert("Server Error: failed to submit submission to bioportal. \n\n"
								+ caught.getMessage());
					}
				});
	}

	private void refreshMatchSubmissionsStatus() {
		globalEventBus.fireEvent(new ProcessingStartEvent(
				"Updating ontology matches and ontology submissions ..."));
		rpcService.refreshOntologyStatus(MainPresenter.uploadID,
				new AsyncCallback<Void>() {

					@Override
					public void onSuccess(Void result) {
						globalEventBus.fireEvent(new ProcessingEndEvent());
						// update the match and submission part
						Window.alert("Updated ontology matches and submissions successfully. ");
						updateMatchesAndSubmissions(selectedTerm,
								selectedCategory);
					}

					@Override
					public void onFailure(Throwable caught) {
						globalEventBus.fireEvent(new ProcessingEndEvent());
						Window.alert("Server Error: failed to Update ontology matches and ontology submissions of terms in this upload. \n\n"
								+ caught.getMessage());
					}
				});
	}

	private void clearSelection() {
		display.getRightPanel().clear();
		rpcService.clearSelection(
				Integer.toString(MainPresenter.uploadInfo.getGlossaryType()),
				selectedTerm, selectedCategory, new AsyncCallback<Void>() {

					@Override
					public void onSuccess(Void result) {
						updateMatchesAndSubmissions(selectedTerm,
								selectedCategory);
						updateTermCategoryPairStatus(MappingStatus.NOT_MAPPED);

					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Server Error: failed in clear selection.\n\n"
								+ caught.getMessage());
					}
				});
	}

	private void dispayAddNewSubmissionView(String term, String category) {
		rpcService.getDefaultDataForNewSubmission(MainPresenter.uploadID, term,
				category, new AsyncCallback<OntologySubmission>() {

					@Override
					public void onSuccess(OntologySubmission result) {
						result.setSubmittedBy(MainPresenter.uploadInfo
								.getEtcUserName());
						new EditSubmissionPresenter(new EditSubmissionView(
								result, OperationType.NEW_SUBMISSION), eventBus)
								.go(display.getRightPanel());
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Server Error: failed in getting default values of a submission. \n\n"
								+ caught.getMessage());
					}
				});
	}

	private void updateTermCategoryPairStatus(MappingStatus status) {
		String styleName = "TO_ONTOLOGY_not_mapped";
		switch (status) {

		case MAPPED_TO_MATCH:
			styleName = "TO_ONTOLOGY_mapped_to_match";
			break;
		case MAPPED_TO_SUBMISSION:
			styleName = "TO_ONTOLOGY_mapped_to_submission";
			break;
		default:
			break;
		}
		selectedPairLabel.removeStyleName("TO_ONTOLOGY_not_mapped");
		selectedPairLabel.removeStyleName("TO_ONTOLOGY_mapped_to_match");
		selectedPairLabel.removeStyleName("TO_ONTOLOGY_mapped_to_submission");
		selectedPairLabel.addStyleName(styleName);
	}

	private void displayOntologyRecordDetail(OntologyRecordType type,
			String recordID) {
		display.getRightPanel().clear();
		if (type.equals(OntologyRecordType.MATCH)) {
			rpcService.getMatchDetail(recordID,
					new AsyncCallback<OntologyMatch>() {

						@Override
						public void onSuccess(OntologyMatch result) {
						//repeating info already shown
							new MatchDetailPresenter(
									new MatchDetailView(result)).go(display
									.getRightPanel());
						}

						@Override
						public void onFailure(Throwable caught) {
							Window.alert("Server Error: " + caught.getMessage());
						}
					});
		} else {
			rpcService.getSubmissionDetail(recordID,
					new AsyncCallback<OntologySubmission>() {

						@Override
						public void onSuccess(OntologySubmission result) {
							new SubmissionDetailPresenter(
									new SubmissionDetailView(result,
											MainPresenter.uploadInfo
													.isHasBioportalInfo()),
									eventBus).go(display.getRightPanel());
						}

						@Override
						public void onFailure(Throwable caught) {
							Window.alert("Server Error: " + caught.getMessage());
						}
					});
		}
	}

	private void updateSelectedOntologyRecord(String recordID,
			final OntologyRecordType type) {
		display.getRightPanel().clear();
		rpcService.updateSelectedOntologyRecord(MainPresenter.uploadID,
				selectedTerm, selectedCategory, recordID, type,
				new AsyncCallback<Void>() {

					@Override
					public void onSuccess(Void result) {
						if (type.equals(OntologyRecordType.MATCH)) {
							updateTermCategoryPairStatus(MappingStatus.MAPPED_TO_MATCH);
						} else {
							updateTermCategoryPairStatus(MappingStatus.MAPPED_TO_SUBMISSION);
						}
						// Window.alert("Selection has been saved!");
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Server Error: error in updating selected ontology record. Please refresh the page and try again later. "
								+ caught.getMessage());
					}
				});
	}

	private void fetchTermsList() {
		Label loading = new Label("select a term from the left.");
		display.getMiddlePanel().setWidget(loading);

		rpcService.getTermCategoryLists(MainPresenter.uploadID,
				new AsyncCallback<TermCategoryLists>() {

					@Override
					public void onSuccess(TermCategoryLists result) {
						display.initiateMiddlePanel();
						updateLists(result);
						
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Server Error: " + caught.getMessage());

					}
				});
	}

	private void moveTermCategoryPair(TermCategoryPair tcPair,
			TermCategoryPairView widget) {
		final TermCategoryPair data = tcPair;
		final TermCategoryPairView w = widget;
		display.getMiddlePanel().clear();
		display.getRightPanel().clear();

		// update the server
		rpcService.moveTermCategoryPair(MainPresenter.uploadID,
				data.getPairID(), !data.isRemoved(), new AsyncCallback<Void>() {

					@Override
					public void onSuccess(Void result) {
						ListType type = ListType.REGULAR_STRUCTURE, related_type = ListType.REMOVED_STRUCTURE;
						if (data.isStructure()) {
							if (data.isRemoved()) {
								type = ListType.REMOVED_STRUCTURE;
								related_type = ListType.REGULAR_STRUCTURE;
							} else {
								type = ListType.REGULAR_STRUCTURE;
								related_type = ListType.REMOVED_STRUCTURE;
							}
						} else {
							if (data.isRemoved()) {
								type = ListType.REMOVED_CHARACTER;
								related_type = ListType.REGULAR_CHARACTER;
							} else {
								type = ListType.REGULAR_CHARACTER;
								related_type = ListType.REMOVED_CHARACTER;
							}
						}
						/**
						 * need to update the lists
						 */
						// 1. remove from current list
						// display.asWidget().removeFromParent();
						w.removeFromParent();
						display.updateTermCategoryPairsCount(type,
								display.getListCountByType(type) - 1);

						// 2. add into the related list
						TermCategoryPair new_data = new TermCategoryPair(data
								.getPairID(), data.getTerm(), data
								.getCategory());
						new_data.setStatus(data.getStatus());
						new_data.setRemoved(!data.isRemoved());
						new_data.setIsStructure(data.isStructure());

						TermCategoryPairView item = new TermCategoryPairView(
								new_data);
						new TermCategoryPairPresenter(item, eventBus,
								globalEventBus).go(display
								.getListPanelByType(related_type));
						display.updateTermCategoryPairsCount(related_type,
								display.getListCountByType(related_type) + 1);
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Server Error: " + caught.getMessage());

					}
				});
	}

	/**
	 * update the middle part of the page
	 * 
	 * @param term
	 * @param category
	 */
	private void updateMatchesAndSubmissions(final String term, String category) {
		if (term == null || category == null || term.equals("")
				|| category.equals("")) {
			return;
		}
		final String selectedTerm = term;
		final String selectedCategory = category;
		display.getRightPanel().clear();
		rpcService.getOntologyRecords(MainPresenter.uploadID, term, category,
				new AsyncCallback<ArrayList<OntologyRecord>>() {

					@Override
					public void onSuccess(ArrayList<OntologyRecord> result) {
						String heading = display.getQueryLabel().getText();
						heading = heading.substring(0, heading.indexOf(":")+1);
						display.getQueryLabel().setText(heading+term);
						new MatchSubmissionPresenter(new MatchSubmissionView(
								result, selectedTerm, selectedCategory,
								MainPresenter.uploadInfo.isHasBioportalInfo(),
								eventBus), eventBus, selectedTerm,
								selectedCategory).go(display.getMiddlePanel());
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Server Error: " + caught.getMessage());
					}
				});
	}

	/**
	 * update the left list view
	 * 
	 * @param lists
	 */
	private void updateLists(TermCategoryLists lists) {
		// structure
		ArrayList<TermCategoryPair> list = lists.getRegularStructures();
		display.updateTermCategoryPairsCount(ListType.REGULAR_STRUCTURE,
				list.size());
		for (TermCategoryPair structure : list) {
			new TermCategoryPairPresenter(new TermCategoryPairView(structure),
					eventBus, globalEventBus).go(display
					.getRegularStructureList());
		}

		// character
		list = lists.getRegularCharacters();
		display.updateTermCategoryPairsCount(ListType.REGULAR_CHARACTER,
				list.size());
		for (TermCategoryPair character : list) {
			new TermCategoryPairPresenter(new TermCategoryPairView(character),
					eventBus, globalEventBus).go(display
					.getRegularCharacterList());
		}

		// removed structure
		list = lists.getRemovedStructures();
		display.updateTermCategoryPairsCount(ListType.REMOVED_STRUCTURE,
				list.size());
		for (TermCategoryPair structure : list) {
			new TermCategoryPairPresenter(new TermCategoryPairView(structure),
					eventBus, globalEventBus).go(display
					.getRemovedStructureList());
		}

		// removed character
		list = lists.getRemovedCharacters();
		display.updateTermCategoryPairsCount(ListType.REMOVED_CHARACTER,
				list.size());
		for (TermCategoryPair character : list) {
			new TermCategoryPairPresenter(new TermCategoryPairView(character),
					eventBus, globalEventBus).go(display
					.getRemovedCharacterList());
		}
	}
}
