(load-file "/work/src/grinder3/src/prj.el")

(jde-set-project-name "The Grinder tests")
(jde-set-variables 
 '(jde-global-classpath
   (append '("../build/tests-classes"
	     "../build/classes")
	   jde-global-classpath))

 '(jde-run-option-classpath jde-global-classpath)

 '(jde-db-source-directories (quote ("../src")))

 '(jde-compile-option-directory "../build/tests-classes")
)
