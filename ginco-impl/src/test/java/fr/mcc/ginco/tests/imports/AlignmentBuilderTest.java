/**
 * Copyright or © or Copr. Ministère Français chargé de la Culture
 * et de la Communication (2013)
 * <p/>
 * contact.gincoculture_at_gouv.fr
 * <p/>
 * This software is a computer program whose purpose is to provide a thesaurus
 * management solution.
 * <p/>
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software. You can use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * <p/>
 * As a counterpart to the access to the source code and rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty and the software's author, the holder of the
 * economic rights, and the successive licensors have only limited liability.
 * <p/>
 * In this respect, the user's attention is drawn to the risks associated
 * with loading, using, modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean that it is complicated to manipulate, and that also
 * therefore means that it is reserved for developers and experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systemsand/or
 * data to be ensured and, more generally, to use and operate it in the
 * same conditions as regards security.
 * <p/>
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package fr.mcc.ginco.tests.imports;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

import fr.mcc.ginco.ark.IIDGeneratorService;
import fr.mcc.ginco.beans.Alignment;
import fr.mcc.ginco.beans.AlignmentType;
import fr.mcc.ginco.beans.ExternalThesaurusType;
import fr.mcc.ginco.beans.ThesaurusConcept;
import fr.mcc.ginco.imports.AlignmentBuilder;
import fr.mcc.ginco.services.IExternalThesaurusService;
import fr.mcc.ginco.services.IExternalThesaurusTypeService;
import fr.mcc.ginco.services.IThesaurusConceptService;
import fr.mcc.ginco.skos.namespaces.SKOS;

public class AlignmentBuilderTest {

	@Mock(name = "generatorService")
	private IIDGeneratorService generatorService;

	@Mock(name = "thesaurusConceptService")
	private IThesaurusConceptService thesaurusConceptService;

	@Mock(name = "externalThesaurusTypeService")
	private IExternalThesaurusTypeService externalThesaurusTypeService;

	@Mock(name = "externalThesaurusService")
	private IExternalThesaurusService externalThesaurusService;

	@InjectMocks
	private AlignmentBuilder alignmentBuilder;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void identifyExternalThesaurusTest() {

		ThesaurusConcept concept = new ThesaurusConcept();
		concept.setIdentifier("http://data.culture.fr/thesaurus/resource/ark:/67717/T69-411");
		
		Model model = ModelFactory.createDefaultModel();
		InputStream is = TermBuilderTest.class
				.getResourceAsStream("/imports/alignments.rdf");
		model.read(is, null);

		Resource skosConcept = model
				.getResource("http://data.culture.fr/thesaurus/resource/ark:/67717/T69-411");

		Statement stmt = skosConcept.getProperty(SKOS.SKOS_ALIGNMENTS.get("=EQ"));
		Statement stmt2 = skosConcept.getProperty(SKOS.SKOS_ALIGNMENTS.get("~EQ"));

		AlignmentType exact = new AlignmentType();
		exact.setIsoCode("=EQ");
		AlignmentType close = new AlignmentType();
		exact.setIsoCode("~EQ");

		List<ExternalThesaurusType> externalThesaurusTypes = new ArrayList<ExternalThesaurusType>();
		ExternalThesaurusType exType = new ExternalThesaurusType();
		exType.setIdentifier(1);
		exType.setLabel("Autorités");
		externalThesaurusTypes.add(exType);

		Mockito.when(externalThesaurusTypeService.getExternalThesaurusTypeList()).thenReturn(externalThesaurusTypes);

		Alignment alignement = alignmentBuilder.buildAlignment(stmt, exact, concept);
		Alignment alignement2 = alignmentBuilder.buildAlignment(stmt2, close, concept);
		Assert.assertEquals(alignement.getExternalTargetThesaurus().getExternalId(), "http://data.bnf.fr/ark:/12148/");
		
		Assert.assertEquals(alignement2.getExternalTargetThesaurus().getExternalId(), "http://dbpedia.org/resources/");
	}
}
