; Copyright (C) 2000 - 2013 Philip Aston
; Copyright (C) 2004 John Stanford White
; All rights reserved.
;
; This file is part of The Grinder software distribution. Refer to
; the file LICENSE which is part of The Grinder distribution for
; licensing details. The Grinder distribution is available on the
; Internet at http://grinder.sourceforge.net/
;
; THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
; "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
; LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
; FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
; COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
; INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
; (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
; SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
; HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
; STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
; ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
; OF THE POSSIBILITY OF SUCH DAMAGE.

{:en {:communication {:local-bind-error "localBindError.text"
                      :send-error "sendError.text"
                      }

      :console {:title "title"
                :terminal-label "shortTitle"

                :section {
                          :console-properties "Console Properties"
                          :file-distribution "File Distribution"
                          :processes "processStatusTableTab.title"
                          :processes-detail "processStatusTableTab.tip"
                          :sampling "options.samplingTab.title"
                          :communication "options.communicationTab.title"
                          :communication-detail "options.communicationTab.tip"
                          :swing-console "Swing Console"
                          :graphs "graphTab.title"
                          :graphs-detail "graphTab.tip"
                          :results "resultsTab.title"
                          :results-detail "resultsTab.tip"
                          :editor "editor.title"
                          :editor-detail "scriptTab.tip"
                          :scripts "scripts.label"

                          :data "Data"
                          :cumulative-data "cumulativeTable.label"
                          :sample-data "sampleTable.label"
                          }

                :option {:chart-statistic "Statistic"
                         :save-defaults "options.save.label"
                         :save-totals-with-results "saveTotalsWithResults.label"
                         :save-totals-with-results.detail "Save totals with results"

                         :sampling :console.section/sampling
                         :sampling-detail "options.samplingTab.tip"
                         :editor :console.section/editor
                         :editor-detail "options.editorTab.tip"
                         :miscellaneous "options.miscellaneousTab.title"
                         :miscellaneous-details "options.miscellaneousTab.tip"

                         :sample-interval "sampleInterval.label"
                         :significant-figures "significantFigures.label"
                         :ignore-sample-count "ignoreSampleCount.label"
                         :ignore-sample-count-detail "Number of samples to ignore"
                         :collect-count-zero "collectCountZero.label"
                         :collect-sample-count "collectSampleCount.label"
                         :collect-sample-count-detail "Number of samples to collect"
                         :console-host "consoleHost.label"
                         :console-port "consolePort.label"
                         :http-host "httpHost.label"
                         :http-port "httpPort.label"
                         :external-editor-command "externalEditorCommand.label"
                         :external-editor-arguments "externalEditorArguments.label"
                         :reset-console-with-processes "resetConsoleWithProcesses.label"
                         :look-and-feel "lookAndFeel.label"

                         :distribution-directory "Script directory"
                         :properties-file "Selected properties file"

                         :frame-bounds "Frame bounds"

                         :default-filename "default.filename"
                         }

                :dialog {:error :console.term/error
                         :error-details "errorDetails.title"
                         :about "about.label"
                         :about.text "about.html"
                         }

                :action {:reset-recording "Reset recording"
                         :start-recording "Start recording"
                         :stop-recording "Stop recording"
                         :set-properties "Set"
                         :ok "error.ok.label"
                         :cancel "options.cancel.label"
                         :copy-to-clipboard "errorDetails.copytoclipboard.label"

                         ; An underscore in the label explicitly marks the next character as a mnemonic.

                         :save-results "save-results.label"
                         :save-results-detail "save-results.tip"

                         :options "options.label"
                         :options-detail "options.tip"

                         :about :console.dialog/about
                         :about-detail :console.dialog/about

                         :exit "exit.label"
                         :exit-detail "exit.label"

                         :start "start.label"
                         :start-detail "start.tip"
                         :stop "stop.label"
                         :stop-detail "stop.tip"

                         :start-processes "start-processes.label"
                         :start-processes-detail "start-processes.tip"
                         :stop-processes "stop-processes.label"
                         :stop-processes-detail "stop-processes.tip"
                         :reset-processes "reset-processes.label"
                         :reset-processes-detail "reset-processes.tip"

                         :new-file "new-file.label"
                         :new-file-detail "new-file.tip"

                         :open-file "open-file.label"
                         :open-file-detail "open-file.tip"

                         :open-file-external "open-file-external.label"
                         :open-file-external-detail "open-file-external.tip"

                         :save-file "save-file.label"
                         :save-file-detail "save-file.tip"

                         :save-file-as "save-file-as.label"
                         :save-file-as-detail "save-file-as.tip"

                         :close-file "close-file.label"
                         :close-file-detail "close-file.tip"

                         :choose-directory "choose-directory.label"
                         :choose-directory-detail "choose-directory.tip"

                         :select-properties "select-properties.label"
                         :select-properties-detail "select-properties.tip"

                         :deselect-properties "deselect-properties.label"
                         :deselect-properties-detail "deselect-properties.tip"

                         :distribute-files "distribute-files.label"
                         :distribute-files-detail "distribute-files.tip"

                         :choose-external-editor "external editor"
                         :choose-external-editor-detail "Select the executable file to use as the external editor"
                         }

                :menu {
                       :file "file.menu.label"
                       :action "action.menu.label"
                       :distribute "distribute.menu.label"
                       :help "help.menu.label"
                       }

                :state {:started "processState.started.label"
                        :running "processState.running.label"
                        :finished "processState.finished.label"
                        :unknown "processState.unknown.label" ; OR "?"
                        :running-agent "processState.connected.label"
                        :finished-agent "processState.disconnected.label"
                        :worker-threads "({0}/{1} {1,choice,0#threads|1#thread|1<{0,number} threads})"
                        :ignoring-samples "Waiting for samples, ignoring"
                        :waiting-for-samples "Waiting for samples"
                        :collection-stopped "Collection stopped"
                        :capturing-samples "Collecting samples"
                        :no-connected-agents "noConnectedAgents.text" ; CAPITAL CASE
                        }

                :term {:agent "processTable.agentProcess.label"
                       :name "Name"
                       :running-processes "Running processes"
                       :status "Status"
                       :test "table.test.label"
                       :tests "test.units" ; CAPITAL CASE
                       :test-number :console.term/test
                       :test-description "table.descriptionColumn.label"
                       :total "totalGraph.title"
                       :totals "Totals"
                       :worker "processTable.workerProcess.label"
                       :peak "graph.averageSuffix.label" ; CAPTIAL CASE
                       :mean "graph.peakSuffix.label" ; CAPITAL CASE
                       :error "error.unit" ; CAPTIAL CASE
                       :errors "error.units" ; CAPITAL CASE
                       :milisecond "ms.unit" ; CAPITAL CASE
                       :miliseconds "ms.units" ; CAPITAL CASE
                       :sample "sample.unit" ; CAPITAL CASE
                       :samples "sample.units" ; CAPITAL CASE
                       :tps "tps.units" ; CAPITAL CASE
                       :threads "processTable.threads.label" ; CAPITAL CASE
                       }

                :phrase {:no-processes "There are no connected agents."
                         :overwrite-confirmation "overwriteConfirmation.text"
                         :out-of-date-overwrite-confirmation "outOfDateOverwriteConfirmation.text"
                         :ignore-existing-buffer-confirmation "ignoreExistingBufferConfirmation.text"
                         :existing-buffer-has-unsaved-changes "existingBufferHasUnsavedChanges.text"
                         :existing-buffer-out-of-date "existingBufferOutOfDate.text"
                         :ignore-existing-buffer-confirmation2 "ignoreExistingBufferConfirmation2.text"
                         :create-directory "createDirectory.text"
                         :file-error "fileError.title"
                         :unexpected-error "unexpectedError.title"
                         :file-write-error "fileWriteError.text"
                         :file-read-error "fileReadError.text"
                         :could-not-load-options-error "couldNotLoadOptionsError.text"
                         :could-not-save-options-error "couldNotSaveOptionsError.text"
                         :new-buffer "newBuffer.text"
                         :save-modified-buffer-confirmation "saveModifiedBufferConfirmation.text"
                         :external-edit-error "externalEditError.text"
                         :external-edit-modified-buffer-confirmation "The file has unsaved changes in an open buffer.\nDo you still want to open it with the external editor?"
                         :collect-negative-error "collectNegativeError.text"
                         :ignore-samples-negative-error "ignoreSamplesNegativeError.text"
                         :interval-less-than-one-error "intervalLessThanOneError.text"
                         :significant-figures-negative-error "significantFiguresNegativeError.text"
                         :unknown-host-error "unknownHostError.text"
                         :invalid-host-address-error "invalidHostAddressError.text."
                         :invalid-port-number-error "invalidPortNumberError.text"
                         :scan-distributioned-files-period-negative-error "scanDistributionFilesPeriodNegativeError.text"
                         :external-editor-not-set "externalEditorNotSet.text"
                         :regular-expression-error "regularExpressionError.text"
                         :reset-console-with-processes-confirmation "resetConsoleWithProcessesConfirmation.text"
                         :dont-ask-me-again "dontAskMeAgain.text"
                         :stop-proceses-confirmation "stopProcessesConfirmation.text"
                         :properties-not-set-confirmation "propertiesNotSetConfirmation.text"
                         :caches-out-of-date-confirmation "cachesOutOfDateConfirmation.text"
                         :start-with-unsaved-buffers-confirmation "startWithUnsavedBuffersConfirmation.text"
                         :script-not-in-directory-error "scriptNotInDirectoryError.text"
                         :save-outside-of-distribution-confirmation "saveOutsideOfDistributionConfirmation.text"
                         ; Add colon
                         :ignoring-unknown-test "Ignoring unknown test"
                         }

                :statistic {:Tests "statistic.Tests.label"
                            :Errors :console.term/errors
                            :Mean_Test_Time_ms "statistic.Mean_Test_Time_(ms).label"
                            :Test_Time_Standard_Deviation_ms "statistic.Test_Time_Standard_Deviation_(ms).label"
                            :TPS :console.term/tps
                            :Peak_TPS "statistic.Peak_TPS.label"
                            :Mean_response_length "statistic.Mean_response_length.label"
                            :Response_bytes_per_second "statistic.Response_bytes_per_second.label"
                            :Response_errors "statistic.Response_errors.label"
                            :Mean_time_to_resolve_host "statistic.Mean_time_to_resolve_host.label"
                            "Mean_time_to_establish_connection" "statistic.Mean_time_to_establish_connection.label"
                            :Mean_time_to_first_byte "statistic.Mean_time_to_first_byte.label"
                            }

                :process {
                          :name "processTable.nameColumn.label"
                          :type "processTable.processTypeColumn.label"
                          :state :console.term/status
                          :label "processTable.processes.label" ; review me
                          }
                }
      :missing  "MISSING TRANSLATION: {0}"
      }
 }
