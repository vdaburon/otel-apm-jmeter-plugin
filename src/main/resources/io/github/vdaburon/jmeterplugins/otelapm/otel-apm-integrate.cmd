@echo off

rem   Licensed to the Apache Software Foundation (ASF) under one or more
rem   contributor license agreements.  See the NOTICE file distributed with
rem   this work for additional information regarding copyright ownership.
rem   The ASF licenses this file to You under the Apache License, Version 2.0
rem   (the "License"); you may not use this file except in compliance with
rem   the License.  You may obtain a copy of the License at
rem
rem       http://www.apache.org/licenses/LICENSE-2.0
rem
rem   Unless required by applicable law or agreed to in writing, software
rem   distributed under the License is distributed on an "AS IS" BASIS,
rem   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
rem   See the License for the specific language governing permissions and
rem   limitations under the License.

rem   Run OTEL ELASTIC APM Integration tool in CLI

rem   run script shell with parameters : <JMETER_HOME>\bin\otel-apm-integrate.cmd -file_in script1.jmx -file_out script1_add.jmx -action ADD -regex .*
rem   run script shell with parameters : <JMETER_HOME>\bin\otel-apm-integrate.cmd -file_in script1_add.jmx -file_out script1_remove.jmx -action REMOVE

setlocal

cd /D %~dp0

set CP=..\lib\ext\otel-apm-jmeter-plugin-${version}-jar-with-dependencies.jar
set CP=%CP%;..\lib\*

java -cp %CP% io.github.vdaburon.jmeter.otelxml.OtelJMeterManager %*
