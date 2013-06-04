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

      :console {:about "about.label"
                :title "title"
                :terminal-label "shortTitle"

                :section {
                          :console-properties "Console Properties"
                          :file-distribution "File Distribution"
                          :processes "processStatusTableTab.title"
                          :processes-detail "processStatusTableTab.tip"
                          :sampling "Sampling"
                          :communication "Communication"
                          :communication-detail "Communication addresses"
                          :swing-console "Swing Console"
                          :graphs "graphTab.title"
                          :graphs-detail "graphTab.tip"
                          :results "resultsTab.title"
                          :results-detail "resultsTab.tip"
                          :editor "scriptTab.title"
                          :editor-detail "scriptTab.tip"
                          :scripts "scripts.label"

                          :data "Data"
                          :cumulative-data "cumulativeTable.label"
                          :sample-data "sampleTable.label"
                          }

                :option {:chart-statistic "Statistic"
                         :save-defaults "Save defaults"
                         :save-totals-with-results "saveTotalsWithResults.label"
                         :save-totals-with-results.detail "Save totals with results"

                         :sampling :console.section/sampling
                         :sampling-detail "Sampling controls"
                         :editor :console.section/editor
                         :editor-detail "Script editor options"
                         :miscellaneous "Miscellaneous"
                         :miscellaneous-details "Other options"

                         :sample-interval "Sample interval"
                         :significant-figures "Significant figures"
                         :ignore-sample-count "Ignore"
                         :ignore-sample-count-detail "Number of samples to ignore"
                         :collect-count-zero "Collect samples forever"
                         :collect-sample-count "Stop after"
                         :collect-sample-count-detail "Number of samples to collect"
                         :console-host "Console Host"
                         :console-port "Console Port"
                         :http-host "HTTP Service Host"
                         :http-port "HTTP Service Port"
                         :external-editor-command "External editor command"
                         :external-editor-arguments "External editor arguments"
                         :reset-console-with-processes "Reset Console with Worker Processes"
                         :look-and-feel "Look and Feel"

                         :distribution-directory "Script directory"
                         :properties-file "Selected properties file"

                         :frame-bounds "Frame bounds"

                         :default-filename "default.filename"
                         }

                :dialog {:error :console.term/error
                         :error-details "errorDetails.title"
                         :about "about.label"
                         :about.text "about.text"
                         }

                :action {:reset-recording "Reset recording"
                         :start-recording "Start recording"
                         :stop-recording "Stop recording"
                         :set-properties "Set"
                         :ok "error.ok.label"
                         :cancel "Cancel"
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

                         :choose-external-editor.label "external editor"
                         :choose-external-editor-detail "Select the executable file to use as the external editor"
                         }

                :menu {
                       :file "file.menu.label"
                       :action "action.menu.label"
                       :distribute "distribute.menu.label"
                       :help "help.menu.label"
                       }

                :state {:started "Ready"
                        :running "Running"
                        :finished "Finished"
                        :unknown "?"
                        :running-agent "Connected"
                        :finished-agent "Disconnected"
                        :worker-threads "({0}/{1} {1,choice,0#threads|1#thread|1<{0,number} threads})"
                        :ignoring-samples "Waiting for samples, ignoring"
                        :waiting-for-samples "Waiting for samples"
                        :collection-stopped "Collection stopped"
                        :capturing-samples "Collecting samples"
                        ; lower case and add <>
                        :noConnectedAgents "No connected agents"
                        }

                :term {:agent "Agent"
                       :name "Name"
                       :running-processes "Running processes"
                       :status "Status"
                       :test "Test"
                       :tests "Tests"
                       :test-number :console.term/test
                       :test-description "Description"
                       :total "totalGraph.title"
                       :totals "Totals"
                       :worker "Worker"
                       :peak "Peak" ; transform to (peak) for usage
                       :mean "Mean" ; transform to (mean) for usage
                       ; transform to lower case where required
                       :error "error.title"
                       :errors "Errors"
                       :miliseconds "ms"
                       :sample "Sample"
                       :samples "Samples"
                       :tps "tps.units"
                       :threads "Threads"
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
                         :dont-ask-me-again "Don't ask me this again"
                         :stop-proceses-confirmation "stopProcessesConfirmation.text"
                         :properties-not-set-confirmation "propertiesNotSetConfirmation.text"
                         :caches-out-of-date-confirmation "cachesOutOfDateConfirmation.text"
                         :start-with-unsaved-buffers-confirmation "startWithUnsavedBuffersConfirmation.text"
                         :script-not-in-directory-error "scriptNotInDirectoryError.text"
                         :save-outside-of-distribution-confirmation "saveOutsideOfDistributionConfirmation.text"
                         ; Add colon
                         :ignoring-unknown-test "Ignoring unknown test"
                         }

                :statistic {:Tests "Successful Tests"
                            :Errors :console.term/errors
                            :Mean_Test_Time_ms "Mean Time"
                            :Test_Time_Standard_Deviation_ms "Mean Time Standard Deviation"
                            :TPS :console.term/tps
                            :Peak_TPS "Peak TPS"
                            :Mean_response_length "Mean Response Length"
                            :Response_bytes_per_second "Response Bytes Per Second"
                            :Response_errors "Response Errors"
                            :Mean_time_to_resolve_host "Mean time to resolve host"
                            "Mean_time_to_establish_connection" "Mean time to establish connection"
                            :Mean_time_to_first_byte "Mean time to first byte"
                            }

                :process {
                          :name "Process"
                          :type "Type"
                          :state :console.term/status
                          :label "worker processes" ; review me
                          }
                }
      :missing  "MISSING TRANSLATION: {0}"
      }
 }
