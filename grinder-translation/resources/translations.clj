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

{:en {:communication {:local-bind-error "Failed to bind to console address, check options."
                      :send-error "Failed to send message, check options."
                      }

      :console {:title "The Grinder Console"
                :terminal-label "console"

                :section {
                          :console-properties "Console Properties"
                          :file-distribution "File Distribution"
                          :processes "Process Control"
                          :processes-detail "Process status"
                          :sampling "Sampling"
                          :communication "Communication"
                          :communication-detail "Communication addresses"
                          :swing-console "Swing Console"
                          :graphs "Graphs"
                          :graphs-detail "Test activity graphs"
                          :results "Results"
                          :results-detail "Tables of test results"
                          :editor "Script Editor"
                          :editor-detail "Edit the test script"
                          :scripts "Scripts"

                          :data "Data"
                          :cumulative-data "Accumulated test statistics"
                          :sample-data "Latest sample"
                          }

                :option {:chart-statistic "Statistic"
                         :save-totals-with-results "Include totals"
                         :save-totals-with-results.detail "Save totals with results"

                         :sampling :console.section/sampling
                         :sampling-detail "Sampling controls"
                         :editor :console.section/editor
                         :editor-detail "Script editor options"
                         :miscellaneous "Miscellaneous"
                         :miscellaneous-detail "Other options"

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

                         :default-filename "grinder-console.data"
                         }

                :dialog {:error :console.term/error
                         :error-details "Error details"
                         :about "About The Grinder"
                         :about.text "about.html"
                         }

                :action {:reset-recording "Reset recording"
                         :start-recording "Start recording"
                         :stop-recording "Stop recording"
                         :set-properties "Set"
                         :ok "OK"
                         :cancel "Cancel"
                         :copy-to-clipboard "Copy to clipboard"
                         :save-defaults "Save defaults"

                         ; An underscore in the label explicitly marks the next character as a mnemonic.

                         :save-results "Save _results"
                         :save-results-detail "Save results"

                         :options "Options"
                         :options-detail "Communications configuration and so on"

                         :about :console.dialog/about
                         :about-detail :console.dialog/about

                         :exit "E_xit"
                         :exit-detail "Exit"

                         :start "Collect statistics"
                         :start-detail "Start collecting statistics"
                         :stop "Stop collection"
                         :stop-detail "Stop collecting statistics"

                         :start-processes "Start workers"
                         :start-processes-detail "Start the worker processes"
                         :stop-processes "Stop agents"
                         :stop-processes-detail "Stop the worker processes and the agent processes"
                         :reset-processes "Stop workers"
                         :reset-processes-detail "Reset the worker processes"

                         :new-file "New file"
                         :new-file-detail "New file"

                         :open-file "Open file"
                         :open-file-detail "Open file"

                         :open-file-external "Open with external editor"
                         :open-file-external-detail "Open file using the configured external editor"

                         :save-file "Save file"
                         :save-file-detail "Save file"

                         :save-file-as "Save file _as"
                         :save-file-as-detail "Save file using a different name"

                         :close-file "Close"
                         :close-file-detail "Close"

                         :choose-directory "Set _directory"
                         :choose-directory-detail "Set the root directory for script distribution"

                         :select-properties "Select _properties"
                         :select-properties-detail "Select the properties file to use. The properties file controls which script will be run."

                         :deselect-properties "Deselect _properties"
                         :deselect-properties-detail "Deselect the properties file. The worker processes will run the script set in the agent's grinder.properties file."

                         :distribute-files "Distribute _files"
                         :distribute-files-detail "Send changed files to worker processes"

                         :choose-external-editor "external editor"
                         :choose-external-editor-detail "Select the executable file to use as the external editor"
                         }

                :menu {
                       :file "File"
                       :action "Action"
                       :distribute "Distribute"
                       :help "Help"
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
                        :no-connected-agents "No connected agents"
                        }

                :term {:agent "Agent"
                       :name "Name"
                       :running-processes "Running processes"
                       :status "Status"
                       :test "Test"
                       :tests "Tests"
                       :test-number :console.term/test
                       :test-description "Description"
                       :total "Total"
                       :totals "Totals"
                       :worker "Worker"
                       :peak "Peak" ; transform to (peak) for usage
                       :mean "Mean" ; transform to (mean) for usage
                       ; transform to lower case where required
                       :error "Error"
                       :errors "Errors"
                       :millisecond "ms"
                       :milliseconds "ms"
                       :sample "Sample"
                       :samples "Samples"
                       :tps "TPS"
                       :threads "Threads"
                       }

                :phrase {:no-processes "There are no connected agents."
                         :overwrite-confirmation "File exists, overwrite?"
                         :out-of-date-overwrite-confirmation "The file has changed on disk and the buffer is not up to date, overwrite?"
                         :ignore-existing-buffer-confirmation "The file is open in another buffer, discard the other buffer?"
                         :existing-buffer-has-unsaved-changes "The other buffer has unsaved changes."
                         :existing-buffer-out-of-date "The file has changed on disk and the other buffer is not up to date."
                         :ignore-existing-buffer-confirmation2 "(If you select 'No' the file will not be saved)."
                         :create-directory "Directory does not exist, create?"
                         :file-error "File Error"
                         :unexpected-error "Unexpected Error"
                         :file-write-error! "Could not write to file \"{0}\"{1}."
                         :file-read-error! "Could not read from file \"{0}\"{1}."
                         :could-not-load-options-error "Could not load console options"
                         :could-not-save-options-error "Could not save console options"
                         :new-buffer "New File"
                         :save-modified-buffer-confirmation! "The text in \"{0}\" has changed.\nDo you want to save the changes?"
                         :external-edit-error "Open in external editor failed, check options."
                         :external-edit-modified-buffer-confirmation "The file has unsaved changes in an open buffer.\nDo you still want to open it with the external editor?"
                         :collect-negative-error! "You must collect at least one sample, zero means \"forever\"."
                         :ignore-samples-negative-error "The number of samples to ignore cannot be negative."
                         :interval-less-than-one-error "Minimum sample interval is 1 ms."
                         :significant-figures-negative-error "Number of significant figures cannot be negative."
                         :unknown-host-error "Unknown host name."
                         :invalid-host-address-error "Invalid IP address. Leave the field blank to bind to all interfaces."
                         :invalid-port-number-error "Port numbers should be in the range [{0}, {1}]."
                         :scan-distributioned-files-period-negative-error "The scan distribution files period cannot be negative."
                         :external-editor-not-set "No external editor has been set, check options."
                         :regular-expression-error "The expression for property {0} is invalid, check options."
                         :reset-console-with-processes-confirmation "You have chosen to reset the worker processes.\nDo you also want to reset the console?"
                         :dont-ask-me-again "Don't ask me this again"
                         :stop-proceses-confirmation "You have chosen to stop all processes, including the agent processes.\nYou will manually have to restart the agent processes. Do you want to continue?"
                         :properties-not-set-confirmation "You have not selected a properties file. The worker processes will run the \nscript set in the agent's grinder.properties file.\nDo you want to continue?"
                         :caches-out-of-date-confirmation "The agent file caches are out of date.\nDo you want to distribute the files now?"
                         :start-with-unsaved-buffers-confirmation "Some buffers have not been saved. Do you want to continue?"
                         :script-not-in-directory-error "The grinder.script property does not refer to a file in the current distribution directory.\nPlease set or correct the 'grinder.script' property."
                         :save-outside-of-distribution-confirmation "The filename you have selected is outside of the current distribution directory.\nDo you really want to save the file here?"
                         :ignoring-unknown-test "Ignoring unknown test"
                         }

                :statistic {:Tests "Successful Tests"
                            :Errors :console.term/errors
                            :Mean-Test-Time-ms "Mean Time"
                            :Test-Time-Standard-Deviation-ms "Mean Time Standard Deviation"
                            :TPS :console.term/tps
                            :Peak-TPS "Peak TPS"
                            :Mean-response-length "Mean Response Length"
                            :Response-bytes-per-second "Response Bytes Per Second"
                            :Response-errors "Response Errors"
                            :Mean-time-to-resolve-host "Mean time to resolve host"
                            "Mean-time-to-establish-connection" "Mean time to establish connection"
                            :Mean-time-to-first-byte "Mean time to first byte"
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
