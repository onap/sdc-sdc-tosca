# ONAP SDC-Tosca


---
---

# Introduction

ONAP SDC-Tosca is delivered as helper JAR that can be used by clients that work with SDC TOSCA CSAR files.
It parses the CSAR and returns the model object which represents the CSAR contents, through designated function calls with SDC flavour.
It uses the underlying generic JTOSCA parser.


# Compiling ONAP JTOSCA

ONAP SDC-Tosca can be compiled easily using maven command: `mvn clean install`
The result is JAR file under "target" folder

# Getting Help

*** to be completed on release ***

SDC@lists.onap.org

SDC Javadoc and Maven site
 
*** to be completed on rrelease ***

# Release notes for versions

1.1.31-SNAPSHOT

Initial after separating into separate repo



-------------------------------

1.1.1-SNAPSHOT

Added toString of Function (GetInput, etc.)

Allowed two arguments for GetInput - name of list input and index in list
