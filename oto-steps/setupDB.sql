/*conventions: always use lower_case for table name for os compatibility purpose*/
create database otosteps;
use otosteps;

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;


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
(0, 'internal texture', 'Internal elements.'),
(0, 'arrangement', 'Disposition of structures with respect to one another within some explicit or implicit standard context.'),
(0, 'behaviour', '''action'' terms such as ''collapsing'' or ''disintegrating'' that are unrelated to plant age or maturity.'),
(0, 'coloration', 'Hue(s), intensity(ies), and/or pattern (if any) of coloring.'),
(0, 'count', 'Number of entities.'),
(0, 'course', 'Shape of course-Linear pattern of the centerline through the length of an axis or vein.'),
(0, 'dehiscence', 'Mode of opening (splitting or forming apertures).'),
(0, 'depth', 'Distance from the top or surface of an entity to its bottom.'),
(0, 'derivation', 'Ontogenetic origin.'),
(0, 'development', 'Mode or pattern of growth and differentiation.'),
(0, 'duration', 'Extent of lifetime, or persistence and physical state after maturation.'),
(0, 'exudation', 'Discharged substance.'),
(0, 'fixation', 'Mode of attachment to a supporting structure.'),
(0, 'fragility', 'Resistance to being damaged or destroyed.'),
(0, 'fusion', 'Physical connection of equivalent or dissimilar structural entities.'),
(0, 'germination', 'The process wherein a dormant embryo or spore resumes active growth.'),
(0, 'habit', 'General appearance and/or function, usually including explicit or implicit reference to one or more aspects of habitat or other features of the external environment.'),
(0, 'height', 'Distance from the base of an entity to the top.'),
(0, 'length', 'Extent of an entity from end to end.'),
(0, 'life_style', 'Mode of life based on life cycle, habit, size and structure of the plant e.g. annual, herb.'),
(0, 'location', 'Position with respect to aspects of environmental context.'),
(0, 'maturation', 'Timing of the attainment of functional maturity, sometimes relative to other structures, sometimes as to constituent structures relative to each other.'),
(0, 'external texture', 'Surface elements including pubescence and coatings.'),
(0, 'nutrition', 'Mode of acquiring nutrients.'),
(0, 'odor', 'Olfactory stimulation.'),
(0, 'orientation', 'Attitude or direction with respect to some explicit or implicit structure(s) or context.'),
(0, 'pattern', 'Exhibiting repetition of placement of its parts.'),
(0, 'position', 'Disposition of a structure with reference to some dissimilar structure(s) or larger context.'),
(0, 'prominence', 'Degree or nature of evidence when present within the context in point.'),
(0, 'volume', 'The amount per unit volume.'),
(0, 'reflectance', 'Aspect as to proportion and pattern of incident light reflected from the surface.'),
(0, 'relief', 'General topographic aspect of a surface. '),
(0, 'reproduction', 'Type, morphology, disposition, function and/or dissemination of reproductive structures.'),
(0, 'shape', 'Overall two- or three-dimensional form or aspect(s) thereof.'),
(0, 'size', 'Nature as to absolute or comparative extent in any one dimension or in area or volume.'),
(0, 'taste', 'The sensation of flavor perceived in the mouth and throat on contact with a substance.'),
(0, 'density', 'The amount per unit area.'),
(0, 'width', 'Extent of an entity from side to side.'),
(0, 'vernation', 'Disposition of leaves in the bud.'),
(0, 'variability', 'An entity''s disposition to vary or change.'),
(0, 'venation', 'Configuration of laminar vasculature as to its hierarchical organization and the dispositions of component orders.'),
(0, 'architecture', 'Organization of the organ’s various parts and the pattern underlying its form.'),
(0, 'condition', 'a state of being'),
(0, 'life_stage', 'development stages of an organism'),
(0, 'organ', 'A part of an organism or an organism as a whole'),
(0, 'season', 'One of the four natural divisions of the year, spring, summer, fall, and winter, in the North and South Temperate zones OR One of the two divisions of the year, rainy and dry, in some tropical regions'),
(0, 'structure', 'Organization of the organ’s various parts and the pattern underlying its form.');

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