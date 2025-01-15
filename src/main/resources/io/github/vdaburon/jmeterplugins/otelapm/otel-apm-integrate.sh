#!/bin/sh

##   Licensed to the Apache Software Foundation (ASF) under one or more
##   contributor license agreements.  See the NOTICE file distributed with
##   this work for additional information regarding copyright ownership.
##   The ASF licenses this file to You under the Apache License, Version 2.0
##   (the "License"); you may not use this file except in compliance with
##   the License.  You may obtain a copy of the License at
##
##       http://www.apache.org/licenses/LICENSE-2.0
##
##   Unless required by applicable law or agreed to in writing, software
##   distributed under the License is distributed on an "AS IS" BASIS,
##   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
##   See the License for the specific language governing permissions and
##   limitations under the License.

##    Run ELASTIC APM Integration tool in CLI

##    run script shell with parameters : <JMETER_HOME>/bin/otel-apm-integrate.sh -file_in script1.jmx -file_out script1_add.jmx -action ADD -regex .*
##    run script shell with parameters : <JMETER_HOME>/bin/otel-apm-integrate.sh -file_in script1_add.jmx -file_out script1_remove.jmx -action REMOVE

cd `dirname $0`

CP=../lib/ext/otel-apm-jmeter-plugin-${version}-jar-with-dependencies.jar
CP=${CP}:../lib/*

java -cp $CP io.soprasteria.vdaburon.jmeter.otelxml.OtelJMeterManager $*
