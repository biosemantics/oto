-- phpMyAdmin SQL Dump
-- version 3.4.10.1deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Jul 30, 2014 at 06:45 PM
-- Server version: 5.5.38
-- PHP Version: 5.3.10-1ubuntu3.13

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `otolite`
--

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

CREATE TABLE IF NOT EXISTS `categories` (
  `uploadID` bigint(20) DEFAULT NULL,
  `category` varchar(50) DEFAULT NULL,
  `definition` varchar(1000) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`uploadID`, `category`, `definition`) VALUES
(0, 'architecture', 'The organization of parts that conform a complex structure and may dictate the form at a macro or micro-morphological level.  Differentiate this category from Arrangement, Shape, and Structure. Architecture emphasizes the composition of an organ (have or have not a part) and the position of its components in it, e.g. antherless,  bimucronate; Arrangement emphasizes the placement of similar organs in space, e.g. clustered, alternate; Shape is the appearance e.g., linear, incurling are shapes of a leaf. When difficult to determine the best category, put the term in two or all three categories. Nouns such as leaves, flowers go into Structure.  The organization of parts that conform a complex structure and may dictate the form at a macro or micro-morphological level.  Differentiate this category from Arrangement, Shape, and Structure. Architecture emphasizes the composition of an organ (have or have not a part) and the position of its components in it, e.g. antherless,  bimucronate; Arrang'),
(0, 'arrangement', 'The disposition of similar structures with respect to one another or in respect with a plane or axis. E.g. whorled, distichous.The disposition of similar structures with respect to one another or in respect with a plane or axis. E.g. whorled, distichous.The disposition of similar structures with respect to one another or in respect with a plane or axis. E.g. whorled, distichous.The disposition of similar structures with respect to one another or in respect with a plane or axis. E.g. whorled, distichous.'),
(0, 'behavior', 'Structure or organism''s behavior aggregate of the responses or reactions or movements in a given situation. E.g. sensitive, fast-growing.Structure or organism''s behavior aggregate of the responses or reactions or movements in a given situation. E.g. sensitive, fast-growing.Structure or organism''s behavior aggregate of the responses or reactions or movements in a given situation. E.g. sensitive, fast-growing.Structure or organism''s behavior aggregate of the responses or reactions or movements in a given situation. E.g. sensitive, fast-growing.'),
(0, 'character', 'An attribute of a structure. This category holds the names of attributes,e.g. color, shape. An attribute of a structure. This category holds the names of attributes,e.g. color, shape. An attribute of a structure. This category holds the names of attributes,e.g. color, shape. An attribute of a structure. This category holds the names of attributes,e.g. color, shape. '),
(0, 'coating', 'A state described as certain substance covering the surface of a structure, e.g, stikcy, dusty, greasy. Differentiate this category from Pubescence, Relief, and Texture. A state described as certain substance covering the surface of a structure, e.g, stikcy, dusty, greasy. Differentiate this category from Pubescence, Relief, and Texture. A state described as certain substance covering the surface of a structure, e.g, stikcy, dusty, greasy. Differentiate this category from Pubescence, Relief, and Texture. A state described as certain substance covering the surface of a structure, e.g, stikcy, dusty, greasy. Differentiate this category from Pubescence, Relief, and Texture. '),
(0, 'substance', 'Physical material existing in a structure or secreted by a structure, such as nector, oil. Differentiate this category from Architecture and Function. Terms describing the presence or absence of a substance, such as starchy or tanniferous should go in Architecture. Terms describing the capability of exudating certain substance, such as oil-secreting go in Function.Physical material existing in a structure or secreted by a structure, such as nector, oil. Differentiate this category from Architecture and Function. Terms describing the presence or absence of a substance, such as starchy or tanniferous should go in Architecture. Terms describing the capability of exudating certain substance, such as oil-secreting go in Function.Physical material existing in a structure or secreted by a structure, such as nector, oil. Differentiate this category from Architecture and Function. Terms describing the presence or absence of a substance, such as starchy or tanniferous should go in Architecture. '),
(0, 'coloration', 'A visual appearance with regard to color, including hue, intensity, or pattern of colors, e.g. dark red, spotted. Differentiate this category with Reflectance. A visual appearance with regard to color, including hue, intensity, or pattern of colors, e.g. dark red, spotted. Differentiate this category with Reflectance. A visual appearance with regard to color, including hue, intensity, or pattern of colors, e.g. dark red, spotted. Differentiate this category with Reflectance. A visual appearance with regard to color, including hue, intensity, or pattern of colors, e.g. dark red, spotted. Differentiate this category with Reflectance. '),
(0, 'condition', 'An affecting state or circumstance, often temporary, a structure or organism is in. E.g., frozen, broken, wilty.An affecting state or circumstance, often temporary, a structure or organism is in. E.g., frozen, broken, wilty.An affecting state or circumstance, often temporary, a structure or organism is in. E.g., frozen, broken, wilty.An affecting state or circumstance, often temporary, a structure or organism is in. E.g., frozen, broken, wilty.'),
(0, 'course', 'Linear, one-dimentional shape of the center line through the length of an axis or vein, e.g. spiraling, straight, zig-zagged.'),
(0, 'dehiscence', ' The mode of opening of a structure, which permits the escape of the content contained within it, e.g.  stegocarpous, schizogenous, undehisced. Differentiate this category from Architecture, which emphasizes structural composition of the parts of a structure, e.g., triporate [3-pores].  The mode of opening of a structure, which permits the escape of the content contained within it, e.g.  stegocarpous, schizogenous, undehisced. Differentiate this category from Architecture, which emphasizes structural composition of the parts of a structure, e.g., triporate [3-pores].  The mode of opening of a structure, which permits the escape of the content contained within it, e.g.  stegocarpous, schizogenous, undehisced. Differentiate this category from Architecture, which emphasizes structural composition of the parts of a structure, e.g., triporate [3-pores].  The mode of opening of a structure, which permits the escape of the content contained within it, e.g.  stegocarpous, schizogenous, undehis'),
(0, 'density', 'The closeness of a group of similar structures are distributed as mass per unit area/size, e.g., dense, sparse.The closeness of a group of similar structures are distributed as mass per unit area/size, e.g., dense, sparse.The closeness of a group of similar structures are distributed as mass per unit area/size, e.g., dense, sparse.The closeness of a group of similar structures are distributed as mass per unit area/size, e.g., dense, sparse.'),
(0, 'depth', 'The distance between upper and lower or between dorsal and ventral points of a structure'),
(0, 'derivation', 'Ontogenetic origin, e.g. adenopetalous, andropetalous.Ontogenetic origin, e.g. adenopetalous, andropetalous.Ontogenetic origin, e.g. adenopetalous, andropetalous.Ontogenetic origin, e.g. adenopetalous, andropetalous.'),
(0, 'development', 'Mode or sequential pattern of growth or differentiation, e.g. centrifugal, centripetal, well-developed, differentiated.Mode or sequential pattern of growth or differentiation, e.g. centrifugal, centripetal, well-developed, differentiated.Mode or sequential pattern of growth or differentiation, e.g. centrifugal, centripetal, well-developed, differentiated.Mode or sequential pattern of growth or differentiation, e.g. centrifugal, centripetal, well-developed, differentiated.'),
(0, 'distribution', 'Geographical names of countries, states or regions, including cardinal directions, e.g., eastern. This category contains proper names, e.g., Arizona. Differentiate this category from Environment.Geographical names of countries, states or regions, including cardinal directions, e.g., eastern. This category contains proper names, e.g., Arizona. Differentiate this category from Environment.Geographical names of countries, states or regions, including cardinal directions, e.g., eastern. This category contains proper names, e.g., Arizona. Differentiate this category from Environment.Geographical names of countries, states or regions, including cardinal directions, e.g., eastern. This category contains proper names, e.g., Arizona. Differentiate this category from Environment.'),
(0, 'duration', 'The extent of lifetime, or persistence and physical state after maturation, e.g., annual, overwintering, deciduous, evergreen.The extent of lifetime, or persistence and physical state after maturation, e.g., annual, overwintering, deciduous, evergreen.The extent of lifetime, or persistence and physical state after maturation, e.g., annual, overwintering, deciduous, evergreen.'),
(0, 'fixation', 'Mode of attachment to a supporting structure, e.g., affixed, adpressed. Differentiate this category from Fusion.Mode of attachment to a supporting structure, e.g., affixed, adpressed. Differentiate this category from Fusion.Mode of attachment to a supporting structure, e.g., affixed, adpressed. Differentiate this category from Fusion.'),
(0, 'fragility', 'Resistance or capacity of being damage or destroyed, e.g. fragile, sturdy.Resistance or capacity of being damage or destroyed, e.g. fragile, sturdy.Resistance or capacity of being damage or destroyed, e.g. fragile, sturdy.'),
(0, 'function', 'Action or activity based on a biological process, e.g., digestive, generative, storage, sensory.Action or activity based on a biological process, e.g., digestive, generative, storage, sensory.Action or activity based on a biological process, e.g., digestive, generative, storage, sensory.'),
(0, 'fusion', 'Physical connection of homologous or non-homologous structures, e.g., subfree, connate, suppressed, unattached, synconnective. Differentiate this category from Fixiation.Physical connection of homologous or non-homologous structures, e.g., subfree, connate, suppressed, unattached, synconnective. Differentiate this category from Fixiation.Physical connection of homologous or non-homologous structures, e.g., subfree, connate, suppressed, unattached, synconnective. Differentiate this category from Fixiation.'),
(0, 'germination', 'A process wherein a dormant embryo or spore resumes active growth, e.g., cryptocotyloid, germinating, phanerocotyloid.A process wherein a dormant embryo or spore resumes active growth, e.g., cryptocotyloid, germinating, phanerocotyloid.A process wherein a dormant embryo or spore resumes active growth, e.g., cryptocotyloid, germinating, phanerocotyloid.'),
(0, 'growth_form', 'The general appearance or function of a whole organism (e.g. growth form of a plant). E.g., tree, shrub, shrubby.The general appearance or function of a whole organism (e.g. growth form of a plant). E.g., tree, shrub, shrubby.The general appearance or function of a whole organism (e.g. growth form of a plant). E.g., tree, shrub, shrubby.'),
(0, 'growth_order', 'Terms that specify the order in which a structure growth in reference to other similar structures. E.g., first, last. Differentiate this category from Development. Terms that specify the order in which a structure growth in reference to other similar structures. E.g., first, last. Differentiate this category from Development. Terms that specify the order in which a structure growth in reference to other similar structures. E.g., first, last. Differentiate this category from Development. '),
(0, 'habitat', 'The immediate environment or substrate where an organism occurs, e.g., wetland, lake, roadside. Differentitate this category from Distribution.The immediate environment or substrate where an organism occurs, e.g., wetland, lake, roadside. Differentitate this category from Distribution.The immediate environment or substrate where an organism occurs, e.g., wetland, lake, roadside. Differentitate this category from Distribution.'),
(0, 'height', 'The distance from the bottom to the top of a structure, e.g., tall, taller, short, shorterThe distance from the bottom to the top of a structure, e.g., tall, taller, short, shorterThe distance from the bottom to the top of a structure, e.g., tall, taller, short, shorter'),
(0, 'length', 'The distance from one end of a structure to the other end, e.g., long, longer.The distance from one end of a structure to the other end, e.g., long, longer.The distance from one end of a structure to the other end, e.g., long, longer.'),
(0, 'life_cycle', 'Life stages or events of a structure or organism, including states after life, e.g. budding, fruiting, dormant, seed-shed, decayed. Life stages or events of a structure or organism, including states after life, e.g. budding, fruiting, dormant, seed-shed, decayed. Life stages or events of a structure or organism, including states after life, e.g. budding, fruiting, dormant, seed-shed, decayed. '),
(0, 'location', 'Position with respects of environmental context, e.g. aerial, submerged. Differentiate this category from Position.Position with respects of environmental context, e.g. aerial, submerged. Differentiate this category from Position.Position with respects of environmental context, e.g. aerial, submerged. Differentiate this category from Position.'),
(0, 'maturation', 'Timing of the attaiment of functional maturity, sometimes relative to other structures, sometimes  as to constituent structures relative to each other. E.g. early-maturing, hysteranthours, protandrous.Timing of the attaiment of functional maturity, sometimes relative to other structures, sometimes  as to constituent structures relative to each other. E.g. early-maturing, hysteranthours, protandrous.Timing of the attaiment of functional maturity, sometimes relative to other structures, sometimes  as to constituent structures relative to each other. E.g. early-maturing, hysteranthours, protandrous.'),
(0, 'nutrition', 'An organism''s mode of acquiring nutrients, e.g., autotrophic, mycoparasitic.An organism''s mode of acquiring nutrients, e.g., autotrophic, mycoparasitic.An organism''s mode of acquiring nutrients, e.g., autotrophic, mycoparasitic.'),
(0, 'odor', 'Olfactory stimulation or the lack of it, e.g.,  scentless, almondy, aromatic, fetid. This category overlap significantly with Taste category.Olfactory stimulation or the lack of it, e.g.,  scentless, almondy, aromatic, fetid. This category overlap significantly with Taste category.Olfactory stimulation or the lack of it, e.g.,  scentless, almondy, aromatic, fetid. This category overlap significantly with Taste category.'),
(0, 'orientation', 'The relative position of a structure to a reference point, line, or/and plane in space, e.g., erect, prostrate.The relative position of a structure to a reference point, line, or/and plane in space, e.g., erect, prostrate.'),
(0, 'ploidy', 'The number of sets of chromosomes in the nucleus of a cell, e.g., diploid, hexaploid, haploid. Note,  "diplods" goes into Structure, because the term refer to organisms with that character state. The number of sets of chromosomes in the nucleus of a cell, e.g., diploid, hexaploid, haploid. Note,  "diplods" goes into Structure, because the term refer to organisms with that character state. '),
(0, 'position', 'The disposition of a structure with reference to some non-homologous (dissimilar) structures or larger context. E.g., adaxial, antipetalous, apical, axillary, basal. Differentiate this category from Location.The disposition of a structure with reference to some non-homologous (dissimilar) structures or larger context. E.g., adaxial, antipetalous, apical, axillary, basal. Differentiate this category from Location.'),
(0, 'position_relational', 'The disposition of a structure in direct relation to other dissimilar structures, with involving structures explicted identified. E.g., [A] covering, covered, embracing, reaching, [B]. The disposition of a structure in direct relation to other dissimilar structures, with involving structures explicted identified. E.g., [A] covering, covered, embracing, reaching, [B]. '),
(0, 'prominence', 'The degree or nature of evidence when present within the context in point, e.g., obscure, unremarkable, prominent.The degree or nature of evidence when present within the context in point, e.g., obscure, unremarkable, prominent.'),
(0, 'pubescence', 'Collective aspect of hairs, scales or bristles born on a surface, e.g., hairy, scaly, wooly. Differentiate this category from Relief, Coating, and Texture. Collective aspect of hairs, scales or bristles born on a surface, e.g., hairy, scaly, wooly. Differentiate this category from Relief, Coating, and Texture. '),
(0, 'quantity', 'The quantity of a structure, e.g. many, few, fewer.The quantity of a structure, e.g. many, few, fewer.'),
(0, 'reflectance', 'Aspect as to proportion and pattern of incident light reflected from the surface, e.g., glassy, glistening, glittering, glossy, polished. Differentiate this category from Coloration.Aspect as to proportion and pattern of incident light reflected from the surface, e.g., glassy, glistening, glittering, glossy, polished. Differentiate this category from Coloration.'),
(0, 'relief', 'General topographic aspect of a surface, e.g., glabrous, sculptured, muricate. Differentiate this category from Pubescence, Coating, and Texture. General topographic aspect of a surface, e.g., glabrous, sculptured, muricate. Differentiate this category from Pubescence, Coating, and Texture. '),
(0, 'reproduction', 'The mode of reproduction or the mode of the development of reproductive organs, including characters of fertility, mating systems, dispersal and pollination strategies, and maturation or distribution of female and/or male flowers. E.g., agamospermous, allogamous, self-fertilizing, polycarpic . Reproductive organs/structures go into Structure.The mode of reproduction or the mode of the development of reproductive organs, including characters of fertility, mating systems, dispersal and pollination strategies, and maturation or distribution of female and/or male flowers. E.g., agamospermous, allogamous, self-fertilizing, polycarpic . Reproductive organs/structures go into Structure.'),
(0, 'season', 'One of the four natural divisions of the year, spring, summer, fall and winter in the North and South Temperate zones OR one of the two divisions of the year, rainy and dry, in some tropical regionsOne of the four natural divisions of the year, spring, summer, fall and winter in the North and South Temperate zones OR one of the two divisions of the year, rainy and dry, in some tropical regions'),
(0, 'shape', 'Overall two- or three – dimensional form or aspect thereof, e.g., rounded, spheroid,  folded,  folding, incurling.Overall two- or three – dimensional form or aspect thereof, e.g., rounded, spheroid,  folded,  folding, incurling.'),
(0, 'size', 'Absolute or relative extent in any one dimension or in an area or volume, e.g., big, medium-sized, small.Absolute or relative extent in any one dimension or in an area or volume, e.g., big, medium-sized, small.'),
(0, 'structure', 'External and internal anatomical entities, including parts, spaces, lines, scars, constrictions, derived products, etc. Terms belonging to this category are nouns, e.g., leaves, stems. Adjective form of structure terms go into "Structure_in_adjective_form" category.External and internal anatomical entities, including parts, spaces, lines, scars, constrictions, derived products, etc. Terms belonging to this category are nouns, e.g., leaves, stems. Adjective form of structure terms go into "Structure_in_adjective_form" category.'),
(0, 'structure_in_adjective_form ', 'External and internal anatomical parts, spaces, lines, and derived products. Terms belonging to this category are adjectives, e.g, bracteal, tigmatic.  Structure terms that are nouns go into "Structure" category.External and internal anatomical parts, spaces, lines, and derived products. Terms belonging to this category are adjectives, e.g, bracteal, tigmatic.  Structure terms that are nouns go into "Structure" category.'),
(0, 'structure_subtype', 'words describing the type of a structure, e.g. primary, '),
(0, 'taste', 'Gustatory stimulation or the lack of it, e.g., acrid, almondy, fruity, spicy. This category overlaps significantly with Odor category.'),
(0, 'taxon_name', 'Taxon and vernacular names, or fragments of the epithet (genus or species). Do not categorize authorities.'),
(0, 'texture', 'Substantial properties as perceived by visual and tactile senses, e.g. bony, fleshy, leathery, papery, cartilaginous. Differentate this category from Pubescence, Relief, and Coating. '),
(0, 'toxicity', 'Degree of toxicity or the lack of it, e.g., irritating, edible, nontoxic, toxic.'),
(0, 'variability', 'Disposition to vary or change, e.g., consistent, diverse, varied.'),
(0, 'width', 'The distance from one side of something to the other side, e.g., wide, broad, narrowed.');

-- --------------------------------------------------------

--
-- Table structure for table `decisions`
--

CREATE TABLE IF NOT EXISTS `decisions` (
  `uploadID` bigint(20) DEFAULT NULL,
  `term` varchar(50) DEFAULT NULL,
  `isMainTerm` tinyint(1) DEFAULT NULL,
  `category` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `ontology_matches`
--

CREATE TABLE IF NOT EXISTS `ontology_matches` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `term` varchar(100) NOT NULL,
  `ontologyID` varchar(100) NOT NULL,
  `permanentID` varchar(100) NOT NULL,
  `parentTerm` varchar(100) DEFAULT NULL,
  `definition` text,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `ID` (`ID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

--
-- Dumping data for table `ontology_matches`
--

INSERT INTO `ontology_matches` (`ID`, `term`, `ontologyID`, `permanentID`, `parentTerm`, `definition`) VALUES
(49, 'petal', 'PO', 'http://purl.obolibrary.org/obo/PO_0009032', 'phyllome;floral organ', 'A phyllome (PO:0006001) that is part of the corolla (PO:0009059), and is usually colored (not green).'),
(50, 'stem', 'PO', 'http://purl.obolibrary.org/obo/PO_0009047', 'shoot axis', 'A shoot axis (PO:0025029) that is the primary axis of a plant.'),
(51, 'flower', 'PO', 'http://purl.obolibrary.org/obo/PO_0009046', 'reproductive shoot system', 'A determinate reproductive shoot system that has as part at least one carpel or at least one stamen and does not contain any other determinate shoot system as a part.'),
(52, 'red', 'PATO', 'http://purl.obolibrary.org/obo/PATO_0000322', 'color', 'A color hue with high wavelength of the long-wave end of the visible spectrum, evoked in the human observer by radiant energy with wavelengths of approximately 630 to 750 nanometers.'),
(53, 'blue', 'PATO', 'http://purl.obolibrary.org/obo/PATO_0000318', 'color', 'A color hue with low wavelength of that portion of the visible spectrum lying between green and indigo, evoked in the human observer by radiant energy with wavelengths of approximately 420 to 490 nanometers.'),
(54, 'round', 'PATO', 'http://purl.obolibrary.org/obo/PATO_0000411', 'elliptic', 'A shape quality inhering in a bearer by virtue of the bearer''s being such that every part of the surface or the circumference is equidistant from the center.'),
(55, 'round', 'PATO', 'http://purl.obolibrary.org/obo/PATO_0002397', '2-D shape', 'A circular shape quality inhering in a bearer by virtue of the bearer''s being nearly, but not perfectly, circular.'),
(56, 'corolla', 'PO', 'http://purl.obolibrary.org/obo/PO_0009059', 'collective phyllome structure', 'A collective phyllome structure (PO:0025023) that is composed of one or more petals (PO:0009032), comprising the inner whorl of non-reproductive floral organs (PO:0025395) and surrounds the androecium (PO:0009061) and the gynoecium (PO:0009062).');

-- --------------------------------------------------------

--
-- Table structure for table `ontology_submissions`
--

CREATE TABLE IF NOT EXISTS `ontology_submissions` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `term` varchar(100) NOT NULL,
  `category` varchar(100) NOT NULL,
  `ontologyID` varchar(100) NOT NULL,
  `submittedBy` varchar(50) DEFAULT NULL,
  `localID` varchar(50) DEFAULT NULL,
  `tmpID` varchar(100) NOT NULL,
  `permanentID` varchar(100) DEFAULT NULL,
  `superClassID` varchar(100) NOT NULL,
  `synonyms` varchar(500) DEFAULT NULL,
  `definition` text NOT NULL,
  `source` varchar(100) NOT NULL,
  `sampleSentence` varchar(500) NOT NULL,
  `accepted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `ID` (`ID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

--
-- Dumping data for table `ontology_submissions`
--

INSERT INTO `ontology_submissions` (`ID`, `term`, `category`, `ontologyID`, `submittedBy`, `localID`, `tmpID`, `permanentID`, `superClassID`, `synonyms`, `definition`, `source`, `sampleSentence`, `accepted`) VALUES
(7, 'round', 'shape', 'PATO', NULL, '0012424f-dc82-4749-88b0-d8758b4b7d3b', 'http://purl.bioontology.org/ontology/provisional/08189b38-4e9e-45ab-a670-245de5157ba4', '', 'PATO:0000001', '', 'test', 'source 1', 'test', 0),
(9, 'red', 'color', 'PATO', 'ETC User 1', '101decf4-369e-11e3-a402-0026b9326338', 'http://purl.bioontology.org/ontology/provisional/a42c2b3b-cf1f-41f8-afd7-f2ccfc8d634b', '', 'PATO:0000001', 'pink, reddish', 'test2', 'source 1', 'test2', 0),
(10, 'petal', 'structure', 'PO', NULL, '0990f34e-56f8-405c-98c9-0303d624880b', 'http://purl.bioontology.org/ontology/provisional/5d3b8350-0b53-4f50-8cad-0caa4b9cc955', '', 'PO:0009012', '', 'test', 'source 1', '', 0);

-- --------------------------------------------------------

--
-- Table structure for table `orders_in_order_category`
--

CREATE TABLE IF NOT EXISTS `orders_in_order_category` (
  `orderID` bigint(20) NOT NULL AUTO_INCREMENT,
  `categoryID` bigint(20) NOT NULL,
  `orderName` varchar(200) DEFAULT NULL,
  `orderDescription` varchar(500) DEFAULT '',
  PRIMARY KEY (`orderID`),
  UNIQUE KEY `orderID` (`orderID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

--
-- Dumping data for table `orders_in_order_category`
--

INSERT INTO `orders_in_order_category` (`orderID`, `categoryID`, `orderName`, `orderDescription`) VALUES
(1, 1, 'Color Order', 'Default Order of Color'),
(2, 2, 'Pubescence-Density Order', 'description of Pubescence-Density Order'),
(3, 3, 'Shape Order', 'explanation of Shape Order'),
(4, 4, 'Shape Order', 'explanation of Orientation Order'),
(5, 1, 'Color Order #2', 'for test only'),
(6, 1, 'test', 'testtt');

-- --------------------------------------------------------

--
-- Table structure for table `order_categories`
--

CREATE TABLE IF NOT EXISTS `order_categories` (
  `categoryID` bigint(20) NOT NULL AUTO_INCREMENT,
  `uploadID` bigint(20) NOT NULL,
  `categoryName` varchar(100) DEFAULT NULL,
  `baseTerm` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`categoryID`),
  UNIQUE KEY `categoryID` (`categoryID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

--
-- Dumping data for table `order_categories`
--

INSERT INTO `order_categories` (`categoryID`, `uploadID`, `categoryName`, `baseTerm`) VALUES
(1, 1, 'Color', NULL),
(2, 1, 'Shape', NULL),
(3, 1, 'Pubescence', NULL),
(4, 1, 'Orientation', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `selected_ontology_records`
--

CREATE TABLE IF NOT EXISTS `selected_ontology_records` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `term` varchar(100) NOT NULL,
  `category` varchar(100) NOT NULL,
  `glossaryType` int(11) NOT NULL,
  `recordType` int(11) NOT NULL,
  `recordID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `ID` (`ID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

--
-- Dumping data for table `selected_ontology_records`
--

INSERT INTO `selected_ontology_records` (`ID`, `term`, `category`, `glossaryType`, `recordType`, `recordID`) VALUES
(249, 'corolla', 'structure', 1, 1, 56);

-- --------------------------------------------------------

--
-- Table structure for table `sentences`
--

CREATE TABLE IF NOT EXISTS `sentences` (
  `uploadID` bigint(20) DEFAULT NULL,
  `sentid` bigint(20) DEFAULT NULL,
  `source` varchar(500) DEFAULT NULL,
  `sentence` varchar(2000) DEFAULT NULL,
  `originalsent` varchar(2000) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `sentences`
--

INSERT INTO `sentences` (`uploadID`, `sentid`, `source`, `sentence`, `originalsent`) VALUES
(155, 1, '1.txt', 'some', 'example');

-- --------------------------------------------------------

--
-- Table structure for table `structures`
--

CREATE TABLE IF NOT EXISTS `structures` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `uploadID` bigint(20) NOT NULL,
  `term` varchar(100) NOT NULL,
  `userCreated` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `ID` (`ID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `synonyms`
--

CREATE TABLE IF NOT EXISTS `synonyms` (
  `uploadID` bigint(20) default NULL,
  `mainTerm` varchar(50) default NULL,
  `synonym` varchar(50) default NULL,
  `category` varchar(50) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `typos` (
  `id` bigint(20) NOT NULL auto_increment,
  `uploadID` int(11) default NULL,
  `originalTerm` varchar(100) default NULL,
  `replacedBy` varchar(100) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `terms`
--

CREATE TABLE IF NOT EXISTS `terms` (
  `uploadID` bigint(20) DEFAULT NULL,
  `term` varchar(50) DEFAULT NULL,
  `type` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `terms`
--

INSERT INTO `terms` (`uploadID`, `term`, `type`) VALUES
(155, 'possstr', 1),
(155, 'possCh', 2),
(155, 'possOth', 3);

-- --------------------------------------------------------

--
-- Table structure for table `terms_in_order_category`
--

CREATE TABLE IF NOT EXISTS `terms_in_order_category` (
  `termID` bigint(20) NOT NULL AUTO_INCREMENT,
  `categoryID` bigint(20) NOT NULL,
  `termName` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`termID`),
  UNIQUE KEY `termID` (`termID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

--
-- Dumping data for table `terms_in_order_category`
--

INSERT INTO `terms_in_order_category` (`termID`, `categoryID`, `termName`) VALUES
(1, 1, 'pink'),
(2, 1, 'white'),
(3, 1, 'red'),
(4, 1, 'green'),
(5, 1, 'blue'),
(6, 1, 'purple'),
(7, 1, 'black'),
(8, 1, 'yellow'),
(9, 2, 'cylindric'),
(10, 2, 'ovoid'),
(11, 2, 'hemispheric'),
(12, 2, 'flat'),
(13, 2, 'convex'),
(14, 2, 'conic'),
(15, 2, 'columnar'),
(16, 2, 'ovate'),
(17, 2, 'lanceolate'),
(18, 2, 'linear'),
(19, 3, 'papillate'),
(20, 3, 'hirsute'),
(21, 3, 'glabrous'),
(22, 3, 'hairy'),
(23, 3, 'bald'),
(24, 3, 'balding'),
(25, 3, 'barbate'),
(26, 3, 'bearded'),
(27, 3, 'bristly'),
(28, 3, 'glabrous'),
(29, 4, 'flat'),
(30, 4, 'erect'),
(31, 4, 'prostrate'),
(32, 4, 'ascending'),
(33, 4, 'spreading'),
(34, 4, 'reflexed'),
(35, 4, 'appressed'),
(36, 4, 'deflexed'),
(37, 1, 'test'),
(38, 1, 'tewtewtwetwetwe');

-- --------------------------------------------------------

--
-- Table structure for table `term_category_pair`
--

CREATE TABLE IF NOT EXISTS `term_category_pair` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `term` varchar(100) NOT NULL,
  `category` varchar(100) NOT NULL,
  `synonyms` varchar(200) DEFAULT '',
  `uploadID` bigint(20) NOT NULL,
  `removed` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `ID` (`ID`),
  KEY `uploadIDIndex` (`uploadID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `term_position_in_order`
--

CREATE TABLE IF NOT EXISTS `term_position_in_order` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `orderID` bigint(20) NOT NULL,
  `termName` varchar(100) NOT NULL,
  `position` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `ID` (`ID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

--
-- Dumping data for table `term_position_in_order`
--

INSERT INTO `term_position_in_order` (`ID`, `orderID`, `termName`, `position`) VALUES
(123, 1, 'pink', 0),
(124, 1, 'white', 1),
(125, 1, 'green', 2),
(126, 1, 'blue', 3),
(127, 1, 'yellow', 4),
(128, 1, 'purple', 5),
(129, 1, 'red', 6),
(130, 1, 'test', 7),
(131, 1, 'black', 8),
(132, 5, 'pink', 0),
(133, 5, 'red', 1),
(134, 5, 'green', 2),
(135, 5, 'blue', 3),
(136, 5, 'purple', 4),
(137, 5, 'black', 5),
(138, 5, 'yellow', 6),
(139, 5, 'tewtewtwetwetwe', 7),
(140, 6, 'white', 0),
(141, 6, 'pink', 1),
(142, 6, 'red', 2),
(143, 6, 'green', 3);

-- --------------------------------------------------------

--
-- Table structure for table `trees`
--

CREATE TABLE IF NOT EXISTS `trees` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `uploadID` bigint(20) NOT NULL,
  `termID` bigint(20) NOT NULL,
  `pID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `ID` (`ID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `types`
--

CREATE TABLE IF NOT EXISTS `types` (
  `typeID` int(11) NOT NULL DEFAULT '0',
  `typeName` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`typeID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `types`
--

INSERT INTO `types` (`typeID`, `typeName`) VALUES
(1, 'structures'),
(2, 'characters'),
(3, 'others');

-- --------------------------------------------------------

--
-- Table structure for table `uploads`
--

CREATE TABLE IF NOT EXISTS `uploads` (
  `uploadID` bigint(20) NOT NULL AUTO_INCREMENT,
  `uploadTime` datetime DEFAULT NULL,
  `sentToOTO` tinyint(1) DEFAULT NULL,
  `isFinalized` tinyint(1) DEFAULT NULL,
  `prefixForOTO` varchar(100) DEFAULT NULL,
  `readyToDelete` datetime DEFAULT NULL,
  `glossaryType` int(11) DEFAULT '1',
  `bioportalUserId` varchar(50) DEFAULT NULL,
  `bioportalApiKey` varchar(100) DEFAULT NULL,
  `EtcUser` varchar(100) DEFAULT NULL,
  `source` varchar(100) DEFAULT NULL,
  `secret` varchar(50) DEFAULT '',
  PRIMARY KEY (`uploadID`),
  UNIQUE KEY `uploadID` (`uploadID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

--
-- Dumping data for table `uploads`
--

INSERT INTO `uploads` (`uploadID`, `uploadTime`, `sentToOTO`, `isFinalized`, `prefixForOTO`, `readyToDelete`, `glossaryType`, `bioportalUserId`, `bioportalApiKey`, `EtcUser`, `source`, `secret`) VALUES
(155, '2014-01-30 12:31:13', 0, 0, 'plants', '2014-01-30 12:31:14', 1, NULL, NULL, NULL, NULL, 'nYl0ut38DlMwCCnzfl0Iiw9c5hs0');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;