package space.myhomework.proxy;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CatalogListing {
	public String id;
	@JsonProperty("short") public String shortTitle;
	public String title;

	@JsonProperty("fall") public boolean offeredFall;
	@JsonProperty("iap") public boolean offeredIAP;
	@JsonProperty("spring") public boolean offeredSpring;

	@JsonProperty("fallI") public String fallInstructors;
	@JsonProperty("springI") public String springInstructors;

	public CatalogListing(ResultSet rs) throws SQLException {
		// see http://web.mit.edu/warehouse/metadata/fields/cis_course_catalog.html

		this.id = rs.getString("SUBJECT_ID");
		this.shortTitle = rs.getString("SUBJECT_SHORT_TITLE");
		this.title = rs.getString("SUBJECT_TITLE");

		this.offeredFall = (rs.getString("IS_OFFERED_FALL_TERM").charAt(0) == 'Y');
		this.offeredIAP = (rs.getString("IS_OFFERED_IAP").charAt(0) == 'Y');
		this.offeredSpring = (rs.getString("IS_OFFERED_SPRING_TERM").charAt(0) == 'Y');

		this.fallInstructors = rs.getString("FALL_INSTRUCTORS");
		this.springInstructors = rs.getString("SPRING_INSTRUCTORS");
	}
}