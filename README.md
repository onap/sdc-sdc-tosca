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

# Testing a CSAR locally
to run the parser locally you can use the MyTest Junit test to easily execute the parser on your own input.

1. to run it place your csar in **sdc-tosca\src\test\resources\csars**

2. Go to the test class located at **sdc-tosca\src\test\java\org\onap\sdc\impl\MyTest.java**

3. un comment the logic ther and update the csar name you plced in the step above:
```java
SdcToscaParserFactory factory = SdcToscaParserFactory.getInstance();
ISdcCsarHelper = getCsarHelper("csars/<your csar name example my.csar>");

//example of functions
//get node type by name
List<NodeTemplate> serviceNodeTemplatesByType = fdntCsarHelper.getServiceNodeTemplatesByType("org.openecomp.nodes.ForwardingPath");
//get node type property
String target_range = fdntCsarHelper.getNodeTemplatePropertyLeafValue(serviceNodeTemplatesByType.get(0), "target_range");

```

# Adding the SDC-Tosca to you project
the SDC tosca is avilalble as a maven depandency.
to use add the folowing depandency to your POM file:
```
<dependency>
  <groupId>org.onap.sdc.sdc-tosca</groupId>
  <artifactId>sdc-tosca</artifactId>
  <version>1.4.6</version>
</dependency>
``` 


# Getting Help


##### [Mailing list](mailto:onap-sdc@lists.onap.org)



##### [JIRA](http://jira.onap.org)



##### [WIKI](https://wiki.onap.org/display/DW/Service+Design+and+Creation+%28SDC%29+Portal)

##### [TOSCA Prser AID]((https://wiki.onap.org/display/DW/Service+Design+and+Creation+%28SDC%29+Portal))



 


# Release notes

## 1.4.1

### Features:
1. **disable max conformance level validation**: until this version the parser had the definition of the max and min conformance level of the CSAR version which it supports. 
   the conformance level is generated by sdc and as a result the two had to be aligned, over wise the CSAR will fail on parsing.
   from this version the max version will no longer be checked by the parser. 
2. **Operations support**: add support for parsing the Operations in the tosca models.  

## 1.4.2

### Features:
1. **enable types validation**  from this version all types in CSAR will be verified, all used types should be properly declared in "base" yml files e.g. node.yml , data.yml etc.

## 1.4.3

### Bug fix:
1. **validation fix**  Maximum nodeTypes we allowed in CSAR file was increased from 10 to 20.

## 1.4.4

### Features:
1. **list support for get input**  until know the get input was only supported in the following format get_input \[ <list > , index ] this way was the only option for using a list typed property and retrieving its value for a use in a property  , from this release you can use get input < list> to retrieve the whole list.

      example:
      ```
      properties:
        # the property type is list
        related_networks:
          #this is now supoorted you can retrive the whole list
          get_input: port_vpg_private_0_port_related_networks
        # the property type is string
        network:
          # this will retrive a value from the list
          get_input:
          - port_vpg_private_0_port_network
          - index_value
      ```

## 1.4.5

### Features:
1. **getPropertyLeafValueByPropertyNamePathAndNodeTemplatePath API** The new Tosca Parser API resolves property value by path in the model (including nested nodes with # delimiter) and property name.

### Code change:
1. Classes FilterType.java, JToscaValidationIssueType.java, SdcTypes.java have been moved from **org.onap.sdc.tosca.parser.impl** package to **org.onap.sdc.tosca.parser.enums** package.

## 1.4.6

### Bug fix:
1. **remove use of a snapshot dependency** update jtosca dependency version from snapshot to release. 

## 1.4.7

### Bug fix:
1. **validation fix**  Null value caused to NullPointerException in validate function in DataEntity


## 1.4.8

### Features:
1. A new API is declared:

*List<IEntityDetails> getEntity(EntityQuery entityQuery, TopologyTemplateQuery topologyTemplateQuery, boolean isRecursive)*

It is designed to retrieve details of one or more entity templates from corresponding topology template according to provided query parameters

2. Mock-up version of **getEntity** API is implemented.

### Note:
This version is intended for SDN-C team usage only.

## 1.5.0

### Changes:
1. Bug fix in Policy metadata object getter (Jtosca)
2. NPE fix in Policy getTargets method (sdc-tosca)
3. Adding more getters to IEntityDetails interface for getEntity API introduced on 1.4.8 version. 
