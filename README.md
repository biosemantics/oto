OTO: Ontology Term Organizer
---------------------
Ontology term organizer collects grouping and relationship opinions from biologists to support consensus decisions. Besides `is_a` and `part_of` relations (the most frequently used relations in biology domains), OTO can be used to sort out ordered values in any domain. 

Demo
---------------------
A demo dataset OTO_demo has been set up at http://biosemantics.arizona.edu/OTO. Use username `OTOdemo` and password `OTOdemopass` to login

Relevant Publications 
---------------------

1. Cui, H., Huang, F., Rodenhausen, T. (2013) Semantic annotation of organism morphological descriptions using CharaParser along with a web-based ontology term organizer (OTO). IEvoBio 2013. Software Demo.

2. Huang, F.Q.,Macklin, J, Morris, P., Sanyal, P.P., Morris, R.A. , Cole, H. & Cui, H (2012) OTO: Ontology Term Organizer. Annual Conference of American Society for Information Science and Technology. [poster]
https://www.asis.org/asist2012/proceedings/Submissions/246.pdf

If you use OTO in your research/work, please cite the above publications.

License
-------

   Copyright 2014 OTO Authors

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.


Project Page
----------
More information is available on the <a href="https://sites.google.com/site/biosemanticsproject/">Fine Grained Semantic Markup</a> and <a href="http://www.etc-project.org">Explorer of Taxon Concepts</a> project pages.

Modules
---------
* oto: the full blows ontology term organizer
* oto-lite: a oto version with a subset of oto's functionality, e.g. used in <a href="https://pods.iplantcollaborative.org/wiki/display/DEapps/CharaParser+Learn">CharaParser found in IPlant's Discovery Environment</a>
* oto-client: client for oto and oto-lite's REST API
* oto-common: shared classes among the modules

Contribution
----------
If you want to contribute, the source is built using Maven
In Eclipse you can therefore use:
* m2e - Maven Integration for Eclipse (e.g. for Juno version: http://download.eclipse.org/releases/juno)
* Run-jetty-run (e.g. http://run-jetty-run.googlecode.com/svn/trunk/updatesite)

Please [configure your git](http://git-scm.com/book/en/Customizing-Git-Git-Configuration) for this repository as:
* `core.autocrlf` true if you are on Windows 
* or `core.autocrlf input` if you are on a Unix-type OS

The target container is Tomcat 5.5.28 for a platform of Java 6.

JavaDoc
----------

Class Diagrams
----------
