/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.espresso;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;

import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertTrue;
import static org.junit.runners.Parameterized.Parameter;
import static org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ToolOptionsIntegrationTest {

	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new IntentsTestRule<>(MainActivity.class);

	@Parameter
	public ToolType toolType;

	@Parameter(1)
	public boolean toolOptionsShownInitially;

	@Parameter(2)
	public boolean hasToolOptions;

	private File testImageFile;

	@Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][]{
				{ToolType.BRUSH, false, true},
				{ToolType.SHAPE, true, true},
				{ToolType.TRANSFORM, true, true},
				{ToolType.LINE, false, true},
				{ToolType.CURSOR, false, true},
				{ToolType.FILL, false, true},
				{ToolType.PIPETTE, false, false},
				{ToolType.STAMP, false, false},
				{ToolType.ERASER, false, true},
				{ToolType.TEXT, true, true},
				{ToolType.IMPORTPNG, false, false}
		});
	}

	@Before
	public void setUp() {
		try {
			testImageFile = File.createTempFile("PocketPaintTest", ".png");
			Bitmap bitmap = Bitmap.createBitmap(25, 25, Bitmap.Config.ARGB_8888);
			OutputStream outputStream = new FileOutputStream(testImageFile);
			assertTrue(bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream));
			outputStream.close();
		} catch (IOException e) {
			throw new AssertionError("Could not create temp file", e);
		}

		Intent intent = new Intent();
		intent.setData(Uri.fromFile(testImageFile));
		Instrumentation.ActivityResult resultOK = new Instrumentation.ActivityResult(Activity.RESULT_OK, intent);
		intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(resultOK);
	}

	@After
	public void tearDown() {
		assertTrue(testImageFile.delete());
	}

	@Test
	public void testToolOptions() {
		onToolBarView()
				.performSelectTool(toolType);

		if (!toolOptionsShownInitially) {
			onToolBarView()
					.performOpenToolOptions();
		}

		if (hasToolOptions) {
			onToolBarView().onToolOptions()
					.check(matches(isDisplayed()));
		} else {
			onToolBarView().onToolOptions()
					.check(matches(not(isDisplayed())));
		}
	}
}
