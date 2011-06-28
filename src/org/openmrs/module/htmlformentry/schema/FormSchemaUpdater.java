package org.openmrs.module.htmlformentry.schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Field;
import org.openmrs.FieldType;
import org.openmrs.Form;
import org.openmrs.FormField;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.htmlformentry.HtmlFormEntryUtil;


/**
 * This class is responsible for updating the core OpenMRS Form Schema,
 * when saving an HTML Form
 * @author jportela
 *
 */
public class FormSchemaUpdater {
	
	
	private HtmlForm htmlForm;
	private FormField obsSection = null;
	private FormField patientSection = null;
	private FormField encounterSection = null;
	private Form form = null;
	private Set<FormField> formSchemaFields = null;
	private FormService formService = null;
	private FieldType sectionType = null;
	private FieldType conceptType = null;
	
    protected final Log log = LogFactory.getLog(getClass());
	
	public FormSchemaUpdater(HtmlForm htmlForm) {
		this.htmlForm = htmlForm;
		form = htmlForm.getForm();
		formSchemaFields = form.getFormFields();
		formService = Context.getFormService();
	}
	
	/** Updates the Core OpenMRS Schema
	 * To be called on HtmlFormEntryServiceImpl.saveHtmlForm
	 */
	public void updateSchema() {
		
		//Retrieves the FormEntrySession
		FormEntrySession session = null;
		try {
			session = new FormEntrySession(HtmlFormEntryUtil.getFakePerson(), htmlForm);
		} catch (Exception e) {	//TODO: Better exception handling
			e.printStackTrace();
		}
        		
        HtmlFormSchema schema = session.getContext().getSchema();	//contains the html form schema
        List <HtmlFormSection> sections = schema.getSections();		//gets a list with all sections of the Html Form
        
		findOrCreateDefaultFieldTypes();
        findOrCreateDefaultSections();
        
        //iterates through the sections
        handleSections(sections, null);
	}
	
	/**
	 * Retrieves the Default sections from the Data Model
	 * If they cannot be found, it will automatically create
	 * new sections on the Data Model
	 */
	private void findOrCreateDefaultSections()
	{
		if (!findDefaultSections())
			createDefaultSections();
	}
	
	/**
	 * Retrieves the OBS, PATIENT and ENCOUNTER sections from the
	 * Data Model
	 * @return true if all sections were found
	 * 
	 * TODO: There's probably a better way for finding the elements
	 * instead of iterating through the list of all fields
	 */
	private boolean findDefaultSections() {
		for (FormField formField : formSchemaFields) {
			String name = formField.getField().getName();
			if (name.equals("OBS"))
				obsSection = formField;
			else if (name.equals("PATIENT"))
				patientSection = formField;
			else if (name.equals("ENCOUNTER"))
				encounterSection = formField;
			
			if (!(obsSection == null || patientSection == null || encounterSection == null))
				return true;
		}
		
		return false;
	}
	
	/**
	 * Populates the "Section" and "Concept" field type from the DB. If it doesn't exist
	 * it will be created.
	 */
	
	private void findOrCreateDefaultFieldTypes() {
		List<FieldType> types = formService.getAllFieldTypes();
		
		//retrieves the Section field type
		for (FieldType type : types) {
			if (type.getName().equals("Section")) {
				sectionType = type;
			}
			else if (type.getName().equals("Concept")) {
				conceptType = type;
			}
			if (!(sectionType == null || conceptType == null))
				break;
		}
		
		//if it could not be found, create a new one
		if (sectionType == null) {
			sectionType = new FieldType();
			sectionType.setName("Section");
			sectionType.setDescription("Section. Generated by HtmlFormEntry module");
			sectionType.setIsSet(true);
			formService.saveFieldType(sectionType);
		}
		if (conceptType == null) {
			conceptType = new FieldType();
			conceptType.setName("Concept");
			conceptType.setDescription("Concept. Generated by HtmlFormEntry module");
			conceptType.setIsSet(true);
			formService.saveFieldType(conceptType);
		}
				
	}

	private FormField createDefaultSection(String name) {
		
		Field sectionField = null;
		
		List<Field> fields = formService.getAllFields();
		
		for (Field field : fields) {
			if (field.getName().equals(name)) {
				sectionField = field;
				break;
			}
		}
		
		if (sectionField == null) {
			sectionField = new Field();
			sectionField.setName(name);
			sectionField.setDescription("Generated by HtmlFormEntry module");
			sectionField.setFieldType(sectionType);
		}
		
		FormField formField = new FormField();
		formField.setField(sectionField);
		formField.setForm(form);
		formField.setName(name);
		formField.setDescription("Generated by HtmlFormEntry module");
		return formService.saveFormField(formField);
				
	}
	
	/**
	 * Creates the Default Sections in the Data Model (OBS,
	 * PATIENT, ENCOUNTER), if they were not found
	 */
	private void createDefaultSections() { 
		 		
		if (obsSection == null) {
			obsSection = createDefaultSection("OBS");
		}
		if (patientSection == null) {
			patientSection = createDefaultSection("PATIENT");
		}
		if (encounterSection == null) {
			encounterSection = createDefaultSection("ENCOUNTER");
		}
		
	}
	
	public void handleSections(List<HtmlFormSection> sections, FormField parent) {
		for (HtmlFormSection section : sections) {
        	
        	//this should add the sections to their place in the form schema
        	//keep in mind that <obs> tags should go into an OBS section, 
        	//and all the other "special" sections (PATIENT, ENCOUNTER)
        	
        	handleSection(section, parent);
        }
	}
	
	public void handleSection(HtmlFormSection section, FormField parent) {
		List<HtmlFormField> fields = section.getFields();
		List<HtmlFormSection> sections = section.getSections();
		
		log.info("HtmlFormSection " + section.getName() + " | " + fields.size() + " fields | " + sections.size() + " sections.");
		
		Field fieldSection = new Field();
		fieldSection.setName("" + section.getName());
		fieldSection.setDescription("Section generated by Html Form Entry Module");
		fieldSection.setFieldType(sectionType);
		
		FormField formField = new FormField();
		formField.setForm(form);
		formField.setField(fieldSection);
		if (parent == null)
			formField.setParent(obsSection);	//TODO add others!
		else
			formField.setParent(parent);
		
		formService.saveFormField(formField);

		

		for (HtmlFormField field : fields) {
			handleField(field, formField);
		}
		
		if (!sections.isEmpty())
			handleSections(sections, formField);
	}
	
	//TODO: Labels for the names?
	public void handleField(HtmlFormField htmlFormField, FormField parent) {
		if (htmlFormField instanceof ObsField) {
    		ObsField obsField = (ObsField) htmlFormField;
    		log.info("ObsField - " + obsField.getQuestion().getName() + " - ConceptID: " + obsField.getQuestion().getId());
    		
    		Field field = new Field();
    		field.setConcept(obsField.getQuestion());
    		field.setName("" + obsField.getQuestion().getName());
    		field.setDescription("Generated by Html Form Entry Module");
    		field.setFieldType(conceptType);
    		
    		FormField formField = new FormField();
    		formField.setForm(form);
    		formField.setField(field);
    		formField.setParent(parent);
    		field.setName("" + obsField.getQuestion().getName());
    		field.setDescription("Generated by Html Form Entry Module");
    		formService.saveFormField(formField);
		
		}
	}

}
