package space.myhomework.proxy;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SubjectOffering {
	public String id;
	public String title;
	public String section;
	public String term;

	@JsonProperty("fake") public boolean fake;
	@JsonProperty("master") public boolean master;

	@JsonProperty("design") public boolean design;
	@JsonProperty("lab") public boolean lab;
	@JsonProperty("lecture") public boolean lecture;
	@JsonProperty("recitation") public boolean recitation;

	@JsonProperty("time") public String time;
	@JsonProperty("place") public String place;

	@JsonProperty("facultyID") public String facultyID;
	@JsonProperty("facultyName") public String facultyName;

	public SubjectOffering(ResultSet rs) throws SQLException {
		// see http://web.mit.edu/warehouse/metadata/fields/subject_offered.html

		this.id = rs.getString("SUBJECT_ID");
		this.title = rs.getString("SUBJECT_TITLE");
		this.section = rs.getString("SECTION_ID");
		this.term = rs.getString("TERM_CODE");

		this.fake = (rs.getString("IS_CREATED_BY_DATA_WAREHOUSE").charAt(0) == 'Y');
		this.master = (rs.getString("IS_MASTER_SECTION").charAt(0) == 'Y');

		this.design = (rs.getString("IS_DESIGN_SECTION").charAt(0) == 'Y');
		this.lab = (rs.getString("IS_LAB_SECTION").charAt(0) == 'Y');
		this.lecture = (rs.getString("IS_LECTURE_SECTION").charAt(0) == 'Y');
		this.recitation = (rs.getString("IS_RECITATION_SECTION").charAt(0) == 'Y');

		this.time = rs.getString("MEET_TIME");
		this.place = rs.getString("MEET_PLACE");

		if (this.time != null) {
			this.time = this.time.trim();
		}
		if (this.place != null) {
			this.place = this.place.trim();
		}

		this.facultyID = rs.getString("RESPONSIBLE_FACULTY_MIT_ID");
		this.facultyName = rs.getString("RESPONSIBLE_FACULTY_NAME");
	}
}