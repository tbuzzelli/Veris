/**
 * 
 */
/**
 * @author tbuzzelli
 *
 */
module veris {
	exports com.verisjudge.ui;
	exports com.verisjudge.checker;
	exports com.verisjudge.utils;
	exports com.verisjudge;

	opens com.verisjudge.ui;
	opens com.verisjudge.checker;
	opens com.verisjudge.utils;
	opens com.verisjudge;

	requires transitive gson;
	requires transitive diffutils;
	requires transitive org.apache.commons.lang3;
	requires transitive javafx.base;
	requires transitive javafx.controls;
	requires transitive javafx.fxml;
	requires transitive javafx.graphics;
	requires transitive javafx.web;
	requires transitive javafx.media;
}