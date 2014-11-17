; Copyright (C) 2000 - 2014 Philip Aston
; Copyright (C) 2004 John Stanford White
; Copyright (C) 2003, 2004 Bertrand Ave
; Copyright (C) 2008 Tagnon Soko
; Copyright (C) 2002, 2003, 2004, 2005 Jose Antonio Zapata Rey
; Copyright (C) 2003 Manuel Silva
; Copyright (C) 2009 Antonio Manuel Muñiz Martín (http://amunizmartin.wordpress.com)
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

{:en {:missing  "MISSING TRANSLATION: {0}"

      :communication {:local-bind-error "Failed to bind to console address, check options."
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

                         :choose-external-editor "External editor"
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
                         :file-write-error! "Could not write to file \"{0}\" {1}."
                         :file-read-error! "Could not read from file \"{0}\" {1}."
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
                          :label "worker processes"
                          }
                }
      }

 :es {:communication {:local-bind-error "Falló la conexión con la consola, verifique las opciones de dirección."
                      :send-error "Error enviando mensaje, verifica las opciones"}
      :console {:state {:finished "Finalizado"
                        :worker-threads_comment "({0}/{1} {1,choice,0#threads|1#thread|1<{0,number} threads}) <translate me>"
                        :capturing-samples "Recopilando muetras"
                        :running "En ejecución"
                        :started "Preparado"
                        :finished-agent "Desconectado"
                        :unknown_comment "? <translate me>"
                        :collection-stopped "Recopilación parada"
                        :waiting-for-samples "Esperando muestras"
                        :ignoring-samples "Esperando muestras, ignorando"
                        :running-agent "Conectado"
                        :no-connected-agents_comment "No connected agents <translate me>"}
                :menu {:file "Archivo"
                       :action "Acción"
                       :distribute "Distribuir"
                       :help "Ayuda"}
                :action {:reset-processes_comment "Stop workers <translate me>"
                         :stop-processes_comment "Stop agents <translate me>"
                         :reset-processes-detail "Inicializar los procesos de trabajo (Worker Processes)"
                         :choose-external-editor-detail "Seleccionar el fichero ejecutable a usar como editor externo"
                         :save-file-detail "Almacenar fichero"
                         :stop-processes-detail "Para los procesos agente y de trabajo"
                         :deselect-properties "Desactivar _properties"
                         :new-file-detail "Nuevo"
                         :about-detail :console.dialog/about
                         :ok "OK"
                         :cancel "Cancelar"
                         :choose-external-editor "Seleccionar un editor externo"
                         :reset-recording_comment "Reset recording <translate me>"
                         :save-file-as-detail "Almacenar usando un nombre diferente"
                         :distribute-files "Distribuir ficheros"
                         :about :console.dialog/about
                         :options-detail "Configuración de comunicaciones, etc..."
                         :choose-directory "Seleccionar directorio"
                         :new-file "Nuevo"
                         :start "Recopilar estadísticas"
                         :start-recording_comment "Start recording <translate me>"
                         :close-file "Cerrar"
                         :open-file-detail "Abrir fichero"
                         :open-file-external-detail "Abrir el fichero usando el editor externo configurado"
                         :save-results-detail "Salvar resultados"
                         :close-file-detail "Cerrar"
                         :exit-detail_comment "Exit <translate me>"
                         :stop "Parar recopilación"
                         :stop-recording_comment "Stop recording <translate me>"
                         :copy-to-clipboard "Copiar al portapapeles"
                         :start-detail "Comenzar la recopilación de estadísticas"
                         :exit "Salir"
                         :set-properties_comment "Set <translate me>"
                         :open-file-external "Abrir con editor externo"
                         :options "Opciones"
                         :distribute-files-detail "Enviar ficheros modificados a los procesos trabajadores"
                         :save-file-as "Almacenar como"
                         :open-file "Abrir fichero"
                         :start-processes_comment "Start workers <translate me>"
                         :save-file "Almacenar"
                         :start-processes-detail "Arrancar los procesos de trabajo (Worker Processes)"
                         :save-defaults "Salvar por defecto"
                         :deselect-properties-detail "Desactiva este fichero de propiedades. Los procesos de trabajo (Worker Processes) ejecutarán el scriptindicado en el fichero grinder.properties del agente."
                         :save-results "Salvar resultados"
                         :stop-detail "Parar la recopilación de estadísticas"
                         :select-properties-detail "Seleccionar el fichero de propiedades a usar. El fichero de propiedades controla el script que será ejecutado."
                         :choose-directory-detail "Seleccionar el directorio raiz para la distribución de scripts"
                         :select-properties "Seleccionar propiedades"}
                :terminal-label "consola"
                :section {:processes-detail "Estado del proceso"
                          :communication-detail "Direcciones de comunicación"
                          :swing-console_comment "Swing Console <translate me>"
                          :cumulative-data "Estadísticas acumuladas de la prueba"
                          :console-properties_comment "Console Properties <translate me>"
                          :results-detail "Tabla de resultados de las pruebas"
                          :scripts "Scripts"
                          :editor-detail "Editar el script de prueba"
                          :sampling "Muestreo"
                          :graphs "Gráficos"
                          :communication "Comunicación"
                          :sample-data "Ultimo muestreo"
                          :editor "Editor de scripts"
                          :file-distribution_comment "File Distribution <translate me>"
                          :graphs-detail "Gráficos de actividad de las pruebas"
                          :results "Resultados"
                          :data_comment "Data <translate me>"
                          :processes_comment "Processes <translate me>"}
                :title "The Grinder - Consola"
                :term {:status "Estado"
                       :test-description "Descripción"
                       :name_comment "Name <translate me>"
                       :sample "muestra"
                       :threads "hilos"
                       :total "Total"
                       :milliseconds "ms"
                       :test "Prueba"
                       :mean "(pico)"
                       :peak "(media)"
                       :samples "muestras"
                       :error "error"
                       :test-number :console.term/test
                       :errors "errores"
                       :millisecond "ms"
                       :running-processes_comment "Running processes <translate me>"
                       :agent "Agente"
                       :tests "transacciones"
                       :tps "TPS"
                       :worker "Trabajador"}
                :process {:name "Proceso"
                          :type "Tipo"
                          :state :console.term/status
                          :label "Procesos de trabajo"}
                :statistic {:Mean-time-to-first-byte "Tiempo medio hasta el primer byte"
                            :Errors :console.term/errors
                            :Mean-response-length "Tamaño medio de las respuestas"
                            :Peak-TPS "TPS pico"
                            :Mean-time-to-resolve-host "Tiempo medio en resolver el host"
                            :Mean-time-to-establish-connection "Tiempo medio en establecer la conexión"
                            :Response-bytes-per-second "Respuesta en bytes por segundo"
                            :Tests "Transacciones exitosas"
                            :Response-errors "Errores de respuesta"
                            :TPS :console.term/tps
                            :Test-Time-Standard-Deviation-ms "Media de la desviación estandar del tiempo"
                            :Mean-Test-Time-ms "Tiempo medio"}
                :phrase {:external-edit-modified-buffer-confirmation "El fichero tiene cambios no salvados en un buffer abierto.\n¿Sigues queriendo abrirlo con el editor externo?"
                         :reset-console-with-processes-confirmation "Has elegido inicializar los procesos de trabajo (Worker Processes)\n?Quieres inicializar tambien la consola?"
                         :ignore-existing-buffer-confirmation2 "(Si seleccionas 'No' el fichero no será almacenado)."
                         :dont-ask-me-again "No volver a preguntar"
                         :ignore-existing-buffer-confirmation "El fichero está abierto en otro buffer, ¿abandonar el otro buffer?"
                         :existing-buffer-has-unsaved-changes "El otro buffer tiene cambios sin salvar"
                         :create-directory "El directorio no existe, ¿crear?"
                         :invalid-port-number-error "El número de los puertos debe estar en el rango [{0}, {1}]."
                         :external-edit-error "Apertura en editor externo fallida, comprueba las opciones."
                         :interval-less-than-one-error "El interválo mínimo de muestreo es de 1 ms."
                         :unexpected-error "Error inesperado"
                         :new-buffer "Nuevo fichero"
                         :save-outside-of-distribution-confirmation "El fichero que has seleccionado está fuera del directorio de distribución actual.\n¿Realmente quieres salvar el fichero aquí?"
                         :overwrite-confirmation "El fichero ya existe, ¿Sobreescribir?"
                         :ignore-samples-negative-error "El número de muestras a ignorar no puede ser negativo."
                         :could-not-save-options-error "No es posible almacenar las opciones de la consola"
                         :existing-buffer-out-of-date "El fichero ha cambiado en disco y el otro buffer no está actualizado"
                         :stop-proceses-confirmation "Has elegido parar todos los procesos, incluyendo los procesos agente.\nTendrás que reiniciar manualmente los procesos agente, ¿Deseas continuar?"
                         :start-with-unsaved-buffers-confirmation "Algunos buffers no han sido almacenados. ¿Desea continuar?"
                         :properties-not-set-confirmation "No has seleccionado un fichero de propiedades. Los procesos de trabajo (Worker Processes) ejecutarán el\nscript indicado en el fichero grinder.peroperties del agente.\n¿Quieres continuar?"
                         :save-modified-buffer-confirmation "El texto en \"{0}\" ha cambiado.\n¿Deseas almacenar los cambios?"
                         :file-read-error "No es posible leer del fichero \"{0}\"{1}."
                         :unknown-host-error "Nombre de host desconocido."
                         :caches-out-of-date-confirmation "Las caches de los agentes no está actualizada.\n¿Quieres distribuir los ficheros ahora?"
                         :ignoring-unknown-test_comment "ignoringUnknownTest.text <translate me>"
                         :could-not-load-options-error "No es posible cargar las opciones de la consola"
                         :out-of-date-overwrite-confirmation "El fichero ha cambiado en disco y el buffer no está actualizado, ¿sobreescribir?"
                         :scan-distributioned-files-period-negative-error "El periodo de escaneo de ficheros de distribución no puede ser negativo."
                         :invalid-host-address-error "Dirección IP inválida. Deje el campo en blanco para enlazar con todas las interfaces."
                         :external-editor-not-set "No se ha seleccionado un editor externo, comprueba las opciones."
                         :file-error "Error en fichero"
                         :file-write-error "No es posible escribir en el fichero \"{0}\"{1}."
                         :significant-figures-negative-error "El número de cifras significativas no puede ser negativo."
                         :script-not-in-directory-error "La propiedad grinder.script no apunta a un fichero contenido en el directorio de distribución actual.\nPor favor, inicializa o corrige la propiedad 'grinder.script'."
                         :no-processes_comment "There are no connected agents. <translate me>"
                         :regular-expression-error "La expresión para la propiedad {0} no es válida, comprueba las opciones."
                         :collect-negative-error "Se debe recopilar al menos una muestra, cero significa \"indefinídamente\"."}
                :option {:chart-statistic_comment "Statistic <translate me>"
                         :external-editor-command "Editor de comandos externo"
                         :collect-count-zero "Recopilar muestras indefinídamente"
                         :ignore-sample-count "Ignorar"
                         :miscellaneous-detail "Otras opciones"
                         :default-filename "grinder-console.data"
                         :console-host "Dirección de la Consola"
                         :reset-console-with-processes "Inicializar la consola con los procesos de trabajo (Worker Processes)"
                         :console-port "Puerto de la Consola"
                         :ignore-sample-count-detail_comment "Number of samples to ignore <translate me>"
                         :collect-sample-count-detail_comment "Number of samples to collect <translate me>"
                         :sample-interval "Intervalo de muestreo"
                         :editor-detail_comment "Script editor options <translate me>"
                         :sampling :console.section/sampling
                         :distribution-directory "Directorio de scripts"
                         :editor :console.section/editor
                         :miscellaneous "Misceláneas"
                         :collect-sample-count "Parar después de"
                         :save-totals-with-results.detail_comment "Save totals with results <translate me>"
                         :external-editor-arguments "Editor de argumentos externo"
                         :properties-file_comment "propertiesFile.label <translate me>"
                         :sampling-detail "Controles de muestreo"
                         :look-and-feel "Look and Feel"
                         :http-host_comment "HTTP Service Host <translate me>"
                         :significant-figures "Cifras significativas"
                         :http-port_comment "HTTP Service Port <translate me>"
                         :save-totals-with-results "Include totals <translate me>"}
                :dialog {:error :console.term/error
                         :error-details "Detalle de errores"
                         :about "Acerca de The Grinder"
                         :about.text "about.html"}}
      }

 :fr {:communication {:local-bind-error "Utilisation de l'adresse de la console impossible, vérifier les options."
                      :send-error "Envoi des messages impossible, vérifier les options."}
      :console {:state {:finished "Arrêté"
                        :worker-threads_comment "({0}/{1} {1,choice,0#threads|1#thread|1<{0,number} threads}) <translate me>"
                        :capturing-samples "Collecte des échantillons en cours"
                        :running "En cours"
                        :started "Prêt"
                        :finished-agent "Déconnecté"
                        :unknown_comment "? <translate me>"
                        :collection-stopped "Collecte arrêtée"
                        :waiting-for-samples "En attente des échantillons"
                        :ignoring-samples "En attente des échantillons, ignorer"
                        :running-agent "Connecté"
                        :no-connected-agents_comment "No connected agents <translate me>"}
                :menu {:file "Fichier"
                       :action "Action"
                       :distribute "Transmettre"
                       :help "Aide"}
                :action {:reset-processes_comment "Stop workers <translate me>"
                         :stop-processes_comment "Stop agents <translate me>"
                         :reset-processes-detail "Ré-initialiser les processus injecteurs"
                         :choose-external-editor-detail "Choisir le programme à utiliser comme étditeur externe."
                         :save-file-detail "Enregistrer"
                         :stop-processes-detail "Arrêter les processus injecteurs et conducteurs"
                         :deselect-properties "Dé-sélectionner le fichier de propriétés"
                         :new-file-detail "Nouveau"
                         :about-detail :console.dialog/about
                         :ok "OK"
                         :cancel "Annuler"
                         :choose-external-editor "Choisir un étditeur externe"
                         :reset-recording_comment "Reset recording <translate me>"
                         :save-file-as-detail "Enregistrer en utilisant un nom différent"
                         :distribute-files "Transmettre les scripts"
                         :about :console.dialog/about
                         :options-detail "Configuration"
                         :choose-directory "Choisir un répertoire"
                         :new-file "Nouveau"
                         :start "Collecter les statistiques"
                         :start-recording_comment "Start recording <translate me>"
                         :close-file "Fermer"
                         :open-file-detail "Ouvrir"
                         :open-file-external-detail "Ouvrir avec l'éditeur externe défini dans les options"
                         :save-results-detail "Enregistrer les résultats"
                         :close-file-detail "Fermer"
                         :exit-detail_comment "Exit <translate me>"
                         :stop "Arrêter la collecte"
                         :stop-recording_comment "Stop recording <translate me>"
                         :copy-to-clipboard "Copier dans le presse-papier"
                         :start-detail "Démarrage de la collecte des statistiques"
                         :exit "Quitter"
                         :set-properties_comment "Set <translate me>"
                         :open-file-external "Ouvrir avec un éditeur externe"
                         :options "Options"
                         :distribute-files-detail "Transmettre les scripts modifiés aux processus injecteurs"
                         :save-file-as "Enregistrer sous"
                         :open-file "Ouvrir"
                         :start-processes_comment "Start workers <translate me>"
                         :save-file "Enregistrer"
                         :start-processes-detail "Démarrer les processus injecteurs"
                         :save-defaults "Enregistrer comme valeurs par défaut"
                         :deselect-properties-detail "Dé-sélectionner le fichier de propriétés. Les processus injecteurs joueront\nle script spécifié dans le fichier de propriétés."
                         :save-results "Enregistrer les résultats"
                         :stop-detail "Arrêter la collecte des statistiques"
                         :select-properties-detail "Choisir le fichier de propriétés à utiliser. Le script qui sera joué est\ndéfini dans le fichier de proptiétés."
                         :choose-directory-detail "Choisir le répertoire racine pour la distribution des scripts."
                         :select-properties "Choisir le fichier de propriétés"}
                :title "The Grinder - La Console"
                :terminal-label "console"
                :section {
                          :processes-detail "Statut des processus"
                          :communication-detail "Adresses de communication"
                          :swing-console_comment "Swing Console <translate me>"
                          :cumulative-data "Résultats consolidés"
                          :console-properties_comment "Console Properties <translate me>"
                          :results-detail "Tableau des résultats"
                          :scripts "Scripts"
                          :editor-detail "Éditer les scripts"
                          :sampling "Échantillonnage"
                          :graphs "Graphiques"
                          :communication "Communication"
                          :sample-data "Dernier échantillon"
                          :editor "Éditeur de scripts"
                          :file-distribution_comment "File Distribution <translate me>"
                          :graphs-detail "Représentations graphiques de l'activité du test"
                          :results "Résultats"
                          :data_comment "Data <translate me>"
                          :processes_comment "Processes <translate me>"}
                :term {:status "État"
                       :test-description "Description"
                       :name_comment "Name <translate me>"
                       :sample "échantillon"
                       :threads "threads"
                       :total "Total"
                       :milliseconds "ms"
                       :test "Test"
                       :mean "pic"
                       :peak "moyenne"
                       :samples "échantillons"
                       :error "erreur"
                       :test-number :console.term/test
                       :errors "erreurs"
                       :millisecond "ms"
                       :running-processes_comment "Running processes <translate me>"
                       :agent "Conducteur"
                       :tests "transactions"
                       :tps "TPS"
                       :worker "Injecteur"}
                :process {:name "Processus"
                          :type "Type"
                          :state :console.term/status
                          :label "Processus injecteurs"}
                :statistic {:Mean-time-to-first-byte "Temps moyen mis pour recevoir le premier byte"
                            :Errors :console.term/errors
                            :Mean-response-length "Taille moyenne de la réponse"
                            :Peak-TPS "Pic TPS"
                            :Mean-time-to-resolve-host "Temps moyen mis pour résoudre le nom d'hôte"
                            "Mean-time-to-establish-connection" "Temps moyen mis pour établir la connexion"
                            :Response-bytes-per-second "Bytes par seconde"
                            :Tests "Transactions réussies"
                            :Response-errors "Nombre d'erreurs"
                            :TPS :console.term/tps
                            :Test-Time-Standard-Deviation-ms "Écart-type du temps moyen de réponse (ms)"
                            :Mean-Test-Time-ms "Temps moyen de réponse (ms)"}
                :phrase {:external-edit-modified-buffer-confirmation "Le fichier a des modifications non enregistrées dans un autre\nprogramme. Voulez-vous toujours l'ouvrir avec l'éditeur externe?"
                         :reset-console-with-processes-confirmation "Vous avez choisi de ré-initialiser les processus injecteurs. Voulez-vous\nré-initialiser la console?"
                         :ignore-existing-buffer-confirmation2 "(Si vous choisissez 'Non' le fichier ne sera pas sauvegardé)."
                         :dont-ask-me-again "Ne plus me demander"
                         :ignore-existing-buffer-confirmation "Le fichier est ouvert dans un autre programme, voulez-vous enregistrer\nla version actuelle?"
                         :existing-buffer-has-unsaved-changes "Certains changements du fichier par l'autre programme n'ont pas été\nenregistrés."
                         :create-directory "Le répertoire n'existe pas, voulez-vous le créer ?"
                         :invalid-port-number-error "Le Port doit être compris dans l'intervalle [{0}, {1}]."
                         :external-edit-error "Échec de l'ouverture du fichier dans l'éditeur externe, vérifier les options."
                         :interval-less-than-one-error "L'intervalle minimum d'échantillonnage est de 1 ms."
                         :unexpected-error "Erreur inattendue"
                         :new-buffer "Nouveau fichier"
                         :save-outside-of-distribution-confirmation "L'emplacement sélectionné pour le fichier est en dehors du dossier de\ndistribution des scripts. Voulez-vous vraiment enregistrer le fichier\nà cet emplacement?"
                         :overwrite-confirmation "Le fichier existe déjà, voulez-vous l'écraser ?"
                         :ignore-samples-negative-error "Le nombre d'échantillons à ignorer ne peut être négatif."
                         :could-not-save-options-error "Les options de la console n'ont pas pû être enregistrées."
                         :existing-buffer-out-of-date "Le fichier ouvert dans l'autre programme n'est plus à jour."
                         :stop-proceses-confirmation "Vous avez choisi d'arrêter tous les processus, conducteurs inclus. Vous\naurez à redémarrer manuellement les conducteurs. Voulez-vous continuer?"
                         :start-with-unsaved-buffers-confirmation "Certains buffers n'ont pas été sauvegardés. Voulez-vous continuer?"
                         :properties-not-set-confirmation "Aucun fichier de propriétés n'a été sélectionné. Les processus injecteurs\nvont jouer le script défini dans le fichier de propriétés du conducteur.\nVoulez-vous continuer?"
                         :save-modified-buffer-confirmation "Le texte \"{0}\" a été modifié. Voulez-vous enregistrer les modifications ?"
                         :file-read-error "Impossible de lire le fichier \"{0}\"{1}."
                         :unknown-host-error "Nom de machine inconnue."
                         :caches-out-of-date-confirmation "Les fichiers dans le cache du conducteur ne sont plus à jour.  Voulez-vous\ndistribuer ces fichiers maintenant?"
                         :ignoring-unknown-test_comment "ignoringUnknownTest.text <translate me>"
                         :could-not-load-options-error "Les options de la console n'ont pas pû être chargées."
                         :out-of-date-overwrite-confirmation "Le fichier a été modifié par un autre programme. Voulez-vous enregistrer la\nversion actuelle?"
                         :scan-distributioned-files-period-negative-error "L'intervalle de temps entre des lancements de processus ne peut pas être\nnégatif."
                         :invalid-host-address-error "Mauvaise adresse IP. Laissez la zone vide pour toutes les adresses de\nla machine."
                         :external-editor-not-set "Aucun éditeur externe n'a été choisi, vérifier les options."
                         :file-error "Erreur sur l'accès à un fichier"
                         :file-write-error "Impossible d'écrire dans le fichier \"{0}\"{1}."
                         :significant-figures-negative-error "Le nombre de décimales ne peut être négatif."
                         :script-not-in-directory-error "La propriété \"grinder.script\" ne fait référence à aucun fichier  du dossier\nde distribution des scripts. Veuillez choisir une valeur correcte pour\nlapropriété \"grinder.script\"."
                         :no-processes_comment "There are no connected agents. <translate me>"
                         :regular-expression-error "L'expression pour la propriété {0} n'est pas valide, vérifier les options."
                         :collect-negative-error "Vous devez collecter au moins un échantillon, 0 (zéro) signifie\n\"collecte ininterrompue\"."}
                :option {:chart-statistic_comment "Statistic <translate me>"
                         :external-editor-command "Programme externe pour l'édition des scripts"
                         :collect-count-zero "Collecte ininterrompue des échantillons"
                         :ignore-sample-count "Ignorer"
                         :miscellaneous-detail "Options diverses"
                         :default-filename "grinder-console.data"
                         :console-host "Adresse de la Console"
                         :reset-console-with-processes "Ré-initialiser la console en même temps que les processus injecteurs"
                         :console-port "Port de la Console"
                         :ignore-sample-count-detail_comment "Number of samples to ignore <translate me>"
                         :collect-sample-count-detail_comment "Number of samples to collect <translate me>"
                         :sample-interval "Intervalle d'échantillonnage"
                         :editor-detail "Options pour l'éditeur de scripts"
                         :sampling :console.section/sampling
                         :distribution-directory "Répertoire des scripts"
                         :editor :console.section/editor
                         :miscellaneous "Divers"
                         :collect-sample-count "Arrêter après"
                         :save-totals-with-results.detail_comment "Save totals with results <translate me>"
                         :external-editor-arguments "Argument (en ligne de commande) du programme externe pour l'édition des scripts"
                         :properties-file_comment "propertiesFile.label <translate me>"
                         :sampling-detail "Configuration de l'échantillonnage"
                         :look-and-feel "Apparence"
                         :http-host_comment "HTTP Service Host <translate me>"
                         :significant-figures "Décimales significatives"
                         :http-port_comment "HTTP Service Port <translate me>"
                         :save-totals-with-results_comment "Include totals <translate me>"}
                :dialog {:error :console.term/error
                         :error-details "Détail des erreurs"
                         :about "A propos de The Grinder"
                         :about.text "about.html"}
                }
      }
 }
